package com.project.forde.service;

import com.project.forde.entity.Board;
import com.project.forde.entity.BoardTag;
import com.project.forde.entity.Tag;
import com.project.forde.entity.composite.BoardTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.BoardTagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardTagService {
    public List<BoardTag> createBoardTag(final Board board, final List<Tag> tags) {
        List<BoardTag> boardTags =  tags.stream().map(tag ->
                BoardTagMapper.INSTANCE.toEntity(new BoardTagPK(board, tag))
        ).toList();

        if (boardTags.isEmpty()) {
            throw new CustomException(ErrorCode.BAD_REQUEST_TAG);
        }

        return boardTags;
    }
}
