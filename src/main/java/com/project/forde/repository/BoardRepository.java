package com.project.forde.repository;

import com.project.forde.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Optional<Board> findByBoardId(Long boardId);
    @EntityGraph(attributePaths = {"uploader"})
    Optional<Board> findBoardIncludeUploaderByBoardId(Long boardId);
    Page<Board> findAllByOrderByCreatedTimeDesc(Pageable pageable);
    Page<Board> findAllByCategoryOrderByCreatedTimeDesc(Pageable pageable, Character type);
    Page<Board> findALlByTitleContainingOrderByCreatedTimeDesc(Pageable pageable, String keyword);
}
