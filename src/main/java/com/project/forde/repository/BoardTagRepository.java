package com.project.forde.repository;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardTag;
import com.project.forde.entity.composite.BoardTagPK;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardTagRepository extends JpaRepository<BoardTag, BoardTagPK> {
    @EntityGraph(attributePaths = {"boardTagPK.tag"})
    List<BoardTag> findAllByBoardTagPK_Board(Board board);
}
