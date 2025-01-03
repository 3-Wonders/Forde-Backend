package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardView;
import com.project.forde.entity.composite.BoardViewPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ViewRepository extends JpaRepository<BoardView, BoardViewPK> {
    Optional<BoardView> findByBoardViewPK(BoardViewPK pk);
}
