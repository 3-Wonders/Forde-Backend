package com.project.forde.repository;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.BoardLike;
import com.project.forde.entity.composite.BoardLikePK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<BoardLike, BoardLikePK> {
    Optional<BoardLike> findByBoardLikePK(BoardLikePK pk);
    List<BoardLike> findAllByBoardLikePK_User(AppUser appUser);
}
