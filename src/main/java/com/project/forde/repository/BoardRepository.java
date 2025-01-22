package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
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
                    "AND FUNCTION('DATE_FORMAT', b.createdTime, '%Y-%m-%d') = FUNCTION('CURDATE') " +
                    "ORDER BY b.viewCount DESC, b.likeCount DESC, b.commentCount DESC, b.boardId DESC "
    )
    Page<Board> findAllByDailyNews(Pageable pageable);

    @Query(
            value = "SELECT b FROM Board b " +
                    "JOIN FETCH b.uploader u " +
                    "WHERE b.category = 'N' " +
                        "AND CAST(FUNCTION('DATE_FORMAT', b.createdTime, '%Y-%m-%d') AS string) " +
                        "LIKE CAST(CONCAT(FUNCTION('DATE_FORMAT', FUNCTION('NOW'), '%Y-%m'), '%') as string) " +
                    "ORDER BY b.viewCount DESC, b.likeCount DESC, b.commentCount DESC, b.boardId DESC "
    )
    Page<Board> findAllByMonthlyNews(Pageable pageable);
}
