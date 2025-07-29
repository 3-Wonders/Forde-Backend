package com.project.forde.service;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardTag;
import com.project.forde.entity.Tag;
import com.project.forde.entity.composite.BoardTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.BoardTagMapper;
import com.project.forde.repository.BoardTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardTagService {
    private final BoardTagRepository boardTagRepository;

    /**
     * 게시글에 태그를 추가하는 메서드
     *
     * @param board 게시글
     * @param tags  태그 리스트
     */
    @Transactional
    public void createBoardTag(final Board board, final List<Tag> tags) {
        List<BoardTag> boardTags =  tags.stream().map(tag ->
                BoardTagMapper.INSTANCE.toEntity(new BoardTagPK(board, tag))
        ).toList();

        if (boardTags.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        boardTagRepository.saveAll(boardTags);
    }
}
