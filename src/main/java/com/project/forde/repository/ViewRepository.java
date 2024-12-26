package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewRepository extends JpaRepository<BoardView, Long> {
    Optional<BoardView> findByUserAndBoard(AppUser user, Board board);
}
