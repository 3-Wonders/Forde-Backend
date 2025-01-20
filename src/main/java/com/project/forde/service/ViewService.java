package com.project.forde.service;

import com.project.forde.entity.AppUser;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardView;
import com.project.forde.entity.composite.BoardViewPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.ViewMapper;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.repository.ViewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final ViewRepository viewRepository;
    private final BoardRepository boardRepository;

    private final AppUserService appUserService;

    @Transactional
    public void createView(Long userId, Long boardId) {
        AppUser user = appUserService.verifyUserAndGet(userId);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        BoardView boardView = viewRepository.findByBoardViewPK(new BoardViewPK(user, board)).orElse(null);

        if (boardView == null) {
            board.setViewCount(board.getViewCount() + 1);

            boardRepository.save(board);
            viewRepository.save(ViewMapper.INSTANCE.toEntity(new BoardViewPK(user, board)));
        }
    }
}
