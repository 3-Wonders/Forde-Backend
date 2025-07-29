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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final AppUserRepository appUserRepository;
    private final ViewRepository viewRepository;
    private final BoardRepository boardRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean createView(Long userId, Long boardId) {
        AppUser user = appUserRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));
        BoardView boardView = viewRepository.findByBoardViewPK(new BoardViewPK(user, board)).orElse(null);

        if (boardView == null) {
            board.setViewCount(board.getViewCount() + 1);

            boardRepository.save(board);
            viewRepository.save(ViewMapper.INSTANCE.toEntity(new BoardViewPK(user, board)));

            return true;
        }

        return false;
    }
}
