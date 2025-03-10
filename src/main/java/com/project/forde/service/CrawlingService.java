package com.project.forde.service;
import com.project.forde.dto.crawling.CrawlingDto;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:crawling.properties")
public class CrawlingService {
    @Value("${news.list.url}")
    private String newsList;
    @Value("${news.base.url}")
    private String newsBaseUrl;

    @Value("${test.id}")
    private long testId;

    private final BoardRepository boardRepository;
    private final AppUserService appUserService;

    /**
     * 게시물(뉴스)을 크롤링 합니다.
     * 첫 번째 사이트에 대한 크롤링 입니다.
     */
    @Transactional
    public void getCrawling() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // 브라우저 사이즈
        WebDriver driver = new ChromeDriver(options);

        driver.get(newsList);

        JavascriptExecutor js = (JavascriptExecutor) driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        int previousSize = 0;
        int newSize = driver.findElements(By.cssSelector("a.card-list-box-main")).size();

        while (newSize > previousSize) { // 페이지 끝까지 무한스크롤
            previousSize = newSize;

            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

            try {
                wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector("a.card-list-box-main"), previousSize));
            } catch (TimeoutException e) {
                log.info("무한 스크롤 페이지의 끝까지 도달");
                break;
            }

            newSize = driver.findElements(By.cssSelector("a.card-list-box-main")).size();
        }

        List<WebElement> boardElements = driver.findElements(By.cssSelector("div.col-md-3.col-sm-4.col-xs-6"));

        List<CrawlingDto> crawlingDtoList = new ArrayList<>();
        for(WebElement boardElement : boardElements) {  // 상세 페이지 + 썸네일 이미지 URL 추출
            // 1. 상세 페이지 URL 추출
            WebElement linkElement = boardElement.findElement(By.cssSelector("a.card-list-box-main.cursor-pointer"));
            String detailUrl = linkElement.getAttribute("href");

            // 2. 이미지 URL 추출
            WebElement imageBox = boardElement.findElement(By.cssSelector("div.card-item-image-box.list-box-transform-image"));
            String styleAttribute = imageBox.getAttribute("style");

            Pattern pattern = Pattern.compile("url\\((.*?)\\)");
            Matcher matcher = pattern.matcher(styleAttribute);
            String imageUrl = "";
            if (matcher.find()) {
                imageUrl = matcher.group(1).replaceAll("\"", ""); // 큰따옴표 제거
            }

            crawlingDtoList.add(new CrawlingDto(detailUrl, imageUrl));
        }

        List<String> boardTitles = boardRepository.findAllDistinctTitles();
        Set<String> checkTitle = new HashSet<>(boardTitles); // 기존 DB의 게시물과 겹치는지 확인
        Pattern excludeTitle = Pattern.compile("\\[|]|아카데미|캠프|안내|모집|국비|교육|과정|센터|스쿨|지원");
        Pattern excludeThumbnail = Pattern.compile("hosoft|background-image|cdn.devsnote.com|ik.imagekit.io");
        List<Board> boards = new ArrayList<>();
        AppUser appUser = appUserService.getUser(testId);

        for (CrawlingDto crawlingDto : crawlingDtoList) {
            String detailUrl = crawlingDto.getDetailUrl();
            driver.get(detailUrl);
            String detailPageSource = driver.getPageSource();
            Document detailPage = Jsoup.parse(detailPageSource);

            // 1. 썸네일 부분 처리
            String thumbnailUrl = crawlingDto.getThumbnailUrl();
            if(excludeThumbnail.matcher(thumbnailUrl).find()) {
                thumbnailUrl = null;
            }

            // 2. 제목 부분 처리
            String title = detailPage.select("div.post-editor-view-title").text();
            if(title.length() > 50) continue;
            if(title.isEmpty()) {
                title = detailPage.select("div.post-comment-title").text();
            }
            if(title.isBlank()) continue;
            if(checkTitle.contains(title)) {
                log.info("중복 게시물 입니다.");
                continue;
            }
            if(excludeTitle.matcher(title).find()) continue;

            // 3. 내용 부분 처리
            try {
                wait.until(driver1 -> js.executeScript("return document.readyState").equals("complete"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toastui-editor-contents")));
            }
            catch (TimeoutException e) {
                log.info("타임아웃 발생");
            }

            String content = detailPage.select("div.toastui-editor-contents").outerHtml();
            if(content.isBlank()) {
                content = detailPage.select("div.post-content.center-block.cke_editable p").outerHtml();
            }
            if(content.isBlank()) {
                log.info("콘텐츠 없음");
                continue;
            }
            content = content.replace("$", "\\$");

            // HTML 특수문자 복원
            content = StringEscapeUtils.unescapeHtml4(content);

            // script 및 style 태그 제거
            Document doc = Jsoup.parse(content);
            doc.select("script, style, textarea").remove();

            // 모든 요소에서 불필요한 속성 제거
            for (Element element : doc.getAllElements()) {
                element.removeAttr("style");
                element.removeAttr("id");
                element.removeAttr("class");

                element.attributes().asList().forEach(attr -> {
                    if (attr.getKey().startsWith("data-")) {
                        element.removeAttr(attr.getKey());
                    }
                });
            }

            // 정리된 HTML을 다시 content에 저장
            content = doc.body().html();


//                String content = getContent(detailUrl, driver);
//                if(content == null) continue;

            // 뉴스 저장
            Board board = new Board();
            board.setCategory('N');
            board.setUploader(appUser);
            board.setTitle(title);
            board.setContent(content);
            board.setThumbnailPath(thumbnailUrl);
            boards.add(board);

        }
        log.info("url 갯수 : {}", crawlingDtoList.size());
        log.info("저장된 뉴스 {}", boards.size());
        boardRepository.saveAll(boards);
    }

    /**
     * 크롤릴을 실행 했을 때(getCrawling() 메소드를 실행 했을 때) 게시물의 내용을 가져오는 부분의 코드가 길어져서 만든 메소드 입니다.
     * 첫 번째 사이트에 대한 크롤링 입니다.
     * @param url 내용을 가져올 url
     * @param driver WebDriver
     * @return 게시물(뉴스)의 내용
     */
    public String getContent(String url, WebDriver driver) {
        try {
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // 페이지가 완전히 로드될 때까지 대기
            wait.until(driver1 -> js.executeScript("return document.readyState").equals("complete"));

            // 특정 요소가 로드될 때까지 대기
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".toastui-editor-contents")));

            // Selenium에서 HTML을 가져와서 Jsoup으로 파싱
            String pageSource = driver.getPageSource();
            Document detailPage = Jsoup.parse(pageSource);

            // 내용 부분 가져오기
            String content = detailPage.select("div.toastui-editor-contents").outerHtml();
            content = content.replace("$", "\\$");

            // HTML 특수문자 복원
            content = StringEscapeUtils.unescapeHtml4(content);

            // script 및 style 태그 제거
            Document doc = Jsoup.parse(content);
            doc.select("script, style, textarea").remove();

            // 모든 요소에서 불필요한 속성 제거
            for (Element element : doc.getAllElements()) {
                element.removeAttr("style");
                element.removeAttr("id");
                element.removeAttr("class");

                element.attributes().asList().forEach(attr -> {
                    if (attr.getKey().startsWith("data-")) {
                        element.removeAttr(attr.getKey());
                    }
                });
            }

            // 정리된 HTML을 다시 content에 저장
            content = doc.body().html();
            return content;
        }
        catch (Exception e) {
            return null;
        }
    }




}
