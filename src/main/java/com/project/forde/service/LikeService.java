package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardLike;
import com.project.forde.entity.composite.BoardLikePK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.LikeMapper;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final AppUserRepository appUserRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void createLike(Long userId, Long boardId) {
        AppUser user = appUserRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        BoardLike like = likeRepository.findByBoardLikePK(new BoardLikePK(user, board)).orElse(null);

        if (like == null) {
            board.setLikeCount(board.getLikeCount() + 1);

            boardRepository.save(board);
            likeRepository.save(LikeMapper.INSTANCE.toEntity(new BoardLikePK(user, board)));
        }
    }

    @Transactional
    public void deleteLike(Long userId, Long boardId) {
        AppUser user = appUserRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        BoardLike like = likeRepository.findByBoardLikePK(new BoardLikePK(user, board)).orElse(null);

        if (like != null) {
            board.setLikeCount(board.getLikeCount() - 1);

            boardRepository.save(board);
            likeRepository.delete(like);
        }
    }
}
