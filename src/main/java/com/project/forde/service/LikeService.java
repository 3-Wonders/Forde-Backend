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
import com.project.forde.type.AppUserCount;
import com.project.forde.type.NotificationTypeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final AppUserRepository appUserRepository;
    private final BoardRepository boardRepository;

    private final NotificationService notificationService;
    private final AppUserService appUserService;

    @Transactional
    public void createLike(Long userId, Long boardId) {
        AppUser user = appUserService.verifyUserAndGet(userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        BoardLike like = likeRepository.findByBoardLikePK(new BoardLikePK(user, board)).orElse(null);

        if (like == null) {
            board.setLikeCount(board.getLikeCount() + 1);

            appUserService.increaseCount(board.getUploader(), AppUserCount.LIKE_COUNT);
            boardRepository.save(board);
            likeRepository.save(LikeMapper.INSTANCE.toEntity(new BoardLikePK(user, board)));

            notificationService.sendNotification(
                    user,
                    board.getUploader(),
                    NotificationTypeEnum.BOARD_LIKE,
                    board,
                    null
            );
        }
    }

    @Transactional
    public void deleteLike(Long userId, Long boardId) {
        AppUser user = appUserService.verifyUserAndGet(userId);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

        BoardLike like = likeRepository.findByBoardLikePK(new BoardLikePK(user, board)).orElse(null);

        if (like != null) {
            board.setLikeCount(board.getLikeCount() - 1);

            appUserService.decreaseCount(board.getUploader(), AppUserCount.LIKE_COUNT);
            boardRepository.save(board);
            likeRepository.delete(like);
        }
    }
}
