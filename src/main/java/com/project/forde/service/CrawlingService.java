package com.project.forde.service;
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
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:crawling.properties")
public class CrawlingService {
    @Value("${news.list.url}")
    private String newsList;
    @Value("${news.base.url}")
    private String newsBaseUrl;
    private final BoardRepository boardRepository;
    private final AppUserService appUserService;

    @Transactional
    public void getCrawling() {
        long userId = 49;
        AppUser appUser = appUserService.getUser(userId);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized"); // 브라우저 사이즈
        WebDriver driver = new ChromeDriver(options);
        List<Board> boards = new ArrayList<>();

        try {
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
                    // 타임아웃 발생 시 반복문을 종료
                    log.info("타임아웃 발생, 더 이상 새 요소를 찾을 수 없습니다.");
                    break;
                }

                newSize = driver.findElements(By.cssSelector("a.card-list-box-main")).size();
            }


            String pageSource = driver.getPageSource();
            Document listDocument = Jsoup.parse(pageSource);
            Elements links = listDocument.select("a.card-list-box-main");

            List<String> urlList = new ArrayList<>();
            List<String> thumbnailList = new ArrayList<>();
            for(Element link : links) {  // 상세 페이지 + 썸네일 이미지 URL 추출
                String url = newsBaseUrl + link.attr("href"); // 게시물 URL
                urlList.add(url);

                // 썸네일 이미지 URL
                Element imageDiv = link.selectFirst("div.card-item-image-box.list-box-transform-image");
                if (imageDiv != null) {
                    String style = imageDiv.attr("style"); // 스타일 속성 가져오기
                    if (style.contains("background-image")) {
                        String thumbnailUrl = style.replaceAll(".*url\\(([^)]+)\\).*", "$1").trim(); // URL 추출
                        thumbnailList.add(thumbnailUrl);
                    }
                }
            }

            int index = 0;
            List<String> boardTitles = boardRepository.findAllDistinctTitles();
            Set<String> checkTitle = new HashSet<>(boardTitles);
            for (String url : urlList) {
                // 썸네일 부분
                String thumbnailUrl = thumbnailList.get(index);
                index++;
                if(!thumbnailUrl.contains("https") ||
                        thumbnailUrl.contains("background-image") ||
                        thumbnailUrl.contains("cdn.devsnote.com") ||
                        thumbnailUrl.contains("ik.imagekit.io")) continue;
                Document detailPage = Jsoup.connect(url).get();

                // 제목 부분
                String title = detailPage.select("div.post-editor-view-title").text();
                if(title.length() > 50) continue;
                if(title.isEmpty()) {
                    title = detailPage.select("div.post-comment-title").text();
                }
                if(title.isBlank()) continue;
                if(checkTitle.contains(title)) continue;

                // 내용 부분
//                String content = detailPage.select("div.toastui-editor-contents").html();
                String content = detailPage.select("#markdown-content .toastui-editor-contents").html();
                content = content.replace("$", "\\$");
                if(content.isBlank()) {
                    log.info("콘텐츠 비어있음");
                    continue;
                }

                content = StringEscapeUtils.unescapeHtml4(content);

                // script 및 style 태그 제거
                Document doc = Jsoup.parse(content);
                doc.select("script, style").remove();

                // 모든 요소에서 불필요한 속성 제거
                for (Element element : doc.getAllElements()) {
                    element.removeAttr("style");
                    element.removeAttr("id");
                    element.removeAttr("class");
                }

                // 정리된 HTML을 다시 content에 저장
                content = doc.body().html();

                byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
                if (contentBytes.length > 65535 || content.isBlank()) {
                    continue;
                }

                Board board = new Board();
                board.setCategory('N');
                board.setUploader(appUser);
                board.setTitle(title);
                board.setContent(content);
                board.setThumbnailPath(thumbnailUrl);
                boards.add(board);
            }
            log.info("url 갯수 : {}", urlList.size());
            log.info("저장된 게시물 : {}", boards.size());
            boardRepository.saveAll(boards);
        } catch (IOException e) {
            log.error("IOException 발생: ", e);
        } finally {
            driver.quit();
        }
    }


}
