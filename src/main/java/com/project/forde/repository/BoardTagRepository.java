package com.project.forde.repository;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardTag;
import com.project.forde.entity.composite.BoardTagPK;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardTagRepository extends JpaRepository<BoardTag, BoardTagPK> {
    @Query(
            "SELECT bt FROM BoardTag bt " +
                    "JOIN FETCH bt.boardTagPK.tag t " +
                    "WHERE bt.boardTagPK.board = :board"
    )
    List<BoardTag> findAllByBoardTagPK_Board(Board board);

    @Query(
            "SELECT bt FROM BoardTag bt " +
                    "JOIN FETCH bt.boardTagPK.tag t " +
                    "WHERE bt.boardTagPK.board IN :boards"
    )
    List<BoardTag> findAllByBoardTagPK_BoardIn(List<Board> boards);
}
