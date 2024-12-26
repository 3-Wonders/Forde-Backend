package com.project.forde.like;

import com.project.forde.AbstractTest;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.repository.BoardRepository;
import com.project.forde.repository.LikeRepository;
import com.project.forde.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@Slf4j
@ExtendWith({ MockitoExtension.class })
public class LikeServiceTest extends AbstractTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private BoardRepository boardRepository;
    @InjectMocks
    private LikeService likeService;

    @Test
    @DisplayName("좋아요 추가")
    void createLike() {
        Mockito.when(appUserRepository.findByUserId(1L)).thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(super.getBoard()));
        Mockito.when(likeRepository.findByUserAndBoard(super.getAppUser(), super.getBoard())).thenReturn(Optional.empty());

        likeService.createLike(1L, 1L);

        Mockito.verify(boardRepository, Mockito.times(1)).save(super.getBoard());
        Mockito.verify(likeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("좋아요 삭제")
    void deleteLike() {
        Mockito.when(appUserRepository.findByUserId(1L)).thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(boardRepository.findById(1L)).thenReturn(Optional.of(super.getBoard()));
        Mockito.when(likeRepository.findByUserAndBoard(super.getAppUser(), super.getBoard())).thenReturn(Optional.of(super.getBoardLike()));

        likeService.deleteLike(1L, 1L);

        Mockito.verify(boardRepository, Mockito.times(1)).save(super.getBoard());
        Mockito.verify(likeRepository, Mockito.times(1)).delete(super.getBoardLike());
    }
}
