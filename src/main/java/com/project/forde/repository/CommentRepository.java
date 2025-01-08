package com.project.forde.repository;

import com.project.forde.entity.Board;
import com.project.forde.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByCommentId(final Long commentId);
    Page<Comment> findAllByBoardOrderByCommentIdDesc(Board board, Pageable pageable);
}
