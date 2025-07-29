package com.project.forde.service;

import com.project.forde.projection.IntroPostProjection;
import com.project.forde.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendService {
    private final BoardRepository boardRepository;

    /**
     * 추천 뉴스를 조회한다.
     * 조회수, 좋아요수, 댓글수 순으로 인기순 게시글 태그에 포함된 10개의 뉴스를 조회한다.
     * 100개의 뉴스를 조회하여 랜덤으로 10개를 추출한다.
     *
     * @return 추천 뉴스 리스트
     */
    public List<IntroPostProjection> getRecommendNews() {
        List<IntroPostProjection> recommendNews = boardRepository.findAllByRecommendNewsInThreeMonth();
        Collections.shuffle(recommendNews);

        return recommendNews.subList(0, 10);
    }
}
