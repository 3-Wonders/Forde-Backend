package com.project.forde.repository;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardTagRepository extends JpaRepository<BoardTag, Long> {
    List<BoardTag> findAllByBoard(Board board);
}
