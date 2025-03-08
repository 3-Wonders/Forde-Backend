package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.projection.IntroPostProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByBoardId(Long boardId);
    @EntityGraph(attributePaths = {"uploader"})
    Optional<Board> findBoardIncludeUploaderByBoardId(Long boardId);
    Page<Board> findAllByOrderByCreatedTimeDesc(Pageable pageable);
    Page<Board> findAllByCategoryOrderByCreatedTimeDesc(Pageable pageable, Character type);
    Page<Board> findALlByTitleContainingOrderByCreatedTimeDesc(Pageable pageable, String keyword);
    Page<Board> findAllByUploaderOrderByCreatedTimeDesc(Pageable pageable, AppUser appuser);

    @Query(
            value = "SELECT b FROM Board b " +
                    "JOIN FETCH b.uploader u " +
                    "WHERE b.uploader.userId IN (" +
                        "SELECT f.followPK.following.userId " +
                        "FROM Follow f " +
                        "WHERE f.followPK.follower.userId = :userId" +
                    ") " +
                    "AND (:type IS NULL OR b.category = :type) " +
                    "ORDER BY b.boardId DESC"
    )
    Page<Board> findAllByCategoryAndFollowingOrderByBoardIdDesc(Pageable pageable, Long userId, @Nullable Character type);

    @Query(
            value = "SELECT b FROM Board b " +
                    "JOIN FETCH b.uploader u " +
                    "JOIN FETCH BoardTag bt ON b.boardId = bt.boardTagPK.board.boardId " +
                    "JOIN FETCH Tag t ON bt.boardTagPK.tag.tagId = t.tagId " +
                    "WHERE t.tagName LIKE CONCAT('%', :tagName, '%') " +
                    "ORDER BY b.boardId DESC"
    )
    Page<Board> findAllByTagNameOrderByCreatedTimeDesc(
            Pageable pageable,
            @Param("tagName") String tagName
    );

    @Query(
            value = "SELECT b FROM Board b " +
                    "JOIN FETCH b.uploader u " +
                    "WHERE b.category = 'N' " +
                    "AND FUNCTION('DATE_FORMAT', b.createdTime, '%Y-%m-%d') = :lastDay " +
                    "ORDER BY b.viewCount DESC, b.likeCount DESC, b.commentCount DESC, b.boardId DESC "
    )
    Page<Board> findAllByDailyNews(Pageable pageable, @Param("lastDay") String lastDay);

    @Query(
            value = "SELECT b FROM Board b " +
                    "JOIN FETCH b.uploader u " +
                    "WHERE b.category = 'N' " +
                        "AND FUNCTION('DATE_FORMAT', b.createdTime, '%Y-%m') = :lastMonth " +
                    "ORDER BY b.viewCount DESC, b.likeCount DESC, b.commentCount DESC, b.boardId DESC "
    )
    Page<Board> findAllByMonthlyNews(Pageable pageable, @Param("lastMonth") String lastMonth);

    @Query(
            value = "WITH ranked_posts AS ( " +
                        "SELECT b.*, u.nickname, u.follower_count, " +
                            "ROW_NUMBER() OVER (PARTITION BY bt.tag_id ORDER BY b.view_count DESC, b.like_count DESC, b.comment_count DESC) AS row_num " +
                        "FROM board b " +
                        "JOIN board_tag bt ON b.board_id = bt.board_id " +
                        "JOIN tag t ON bt.tag_id = t.tag_id " +
                        "JOIN app_user u ON b.uploader_id = u.user_id " +
                        "WHERE b.created_time BETWEEN DATE_ADD(NOW(), INTERVAL -3 MONTH) AND NOW() " +
                            "AND b.category = 'N' " +
                    ") " +
                    "SELECT board_id, title, thumbnail_path AS thumbnail, nickname AS nickname FROM ranked_posts " +
                    "WHERE row_num <= 10 " +
                    "ORDER BY row_num ASC, view_count DESC, follower_count DESC, like_count DESC, comment_count DESC " +
                    "LIMIT 100;"
            , nativeQuery = true
    )
    List<IntroPostProjection> findAllByRecommendNewsInThreeMonth();

    @Query(
            value = "SELECT b.boardId AS boardId, b.title AS title, b.thumbnailPath AS thumbnail, u.nickname AS nickname FROM Board b " +
                    "JOIN b.uploader u " +
                    "WHERE b.category <> 'N' " +
                        "AND FUNCTION('DATE_FORMAT', b.createdTime, '%Y-%m') = :lastMonth " +
                    "ORDER BY b.viewCount DESC, b.likeCount DESC, b.commentCount DESC, b.boardId DESC " +
                    "LIMIT 10"
    )
    List<IntroPostProjection> findAllByMonthlyPosts(@Param("lastMonth") String lastMonth);
}
