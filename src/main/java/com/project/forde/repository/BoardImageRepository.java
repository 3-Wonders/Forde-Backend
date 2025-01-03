package com.project.forde.repository;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
    List<BoardImage> findAllByBoard(Board board);
    List<BoardImage> findAllByImageIdIn(List<Long> imageIds);
}
