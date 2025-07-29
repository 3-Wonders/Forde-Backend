package com.project.forde.view;

import com.project.forde.AbstractTest;
import com.project.forde.entity.composite.BoardViewPK;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.repository.ViewRepository;
import com.project.forde.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@Slf4j
@ExtendWith({ MockitoExtension.class })
public class ViewServiceTest extends AbstractTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private ViewRepository viewRepository;
    @InjectMocks
    private ViewService viewService;

    @Test
    @DisplayName("게시글 조회수 증가")
    void increaseViewCount() {
        Long userId = 1L;
        Long boardId = 1L;

        Mockito.when(appUserRepository.findByUserId(userId)).thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(boardRepository.findById(boardId)).thenReturn(Optional.of(super.getBoard()));
        Mockito.when(viewRepository.findByBoardViewPK(new BoardViewPK(super.getAppUser(), super.getBoard()))).thenReturn(Optional.empty());

        // then
        viewService.createView(userId, boardId);

        Mockito.verify(boardRepository, Mockito.times(1)).save(super.getBoard());
        Mockito.verify(viewRepository, Mockito.times(1)).save(ArgumentMatchers.any());
    }
}
