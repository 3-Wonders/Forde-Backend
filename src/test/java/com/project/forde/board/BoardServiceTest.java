package com.project.forde.board;

import com.project.forde.AbstractTest;
import com.project.forde.dto.FileDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.entity.Board;
import com.project.forde.entity.BoardImage;
import com.project.forde.entity.Tag;
import com.project.forde.exception.CustomException;
import com.project.forde.repository.*;
import com.project.forde.service.BoardService;
import com.project.forde.type.SortBoardTypeEnum;
import com.project.forde.util.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@ExtendWith({ MockitoExtension.class })
public class BoardServiceTest extends AbstractTest {
    @Mock
    private FileStore fileStore;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private BoardTagRepository boardTagRepository;
    @Mock
    private BoardImageRepository boardImageRepository;
    @InjectMocks
    private BoardService boardService;

    @Test
    @DisplayName("게시글 생성 실패 - 유저 없음")
    void createBoardFailedWithNotFound() {
        Mockito.when(appUserRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CustomException.class, () -> {
            boardService.create(new BoardDto.Request.Create("N", "제목1", "내용1", List.of(1L), null, null));
        });

        Mockito.verify(boardRepository, Mockito.never()).save(ArgumentMatchers.any(Board.class));
    }

    @Test
    @DisplayName("게시글 생성 실패 - 태그 3개 초과")
    void createBoardFailedWithTag() {
        Mockito.when(appUserRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(tagRepository.findAllByTagIdIn(Mockito.anyList()))
                .thenReturn(List.of(new Tag[]{ super.getTag(), super.getTag(), super.getTag(), super.getTag() }));

        Assertions.assertThrows(CustomException.class, () -> {
            boardService.create(new BoardDto.Request.Create("N", "제목1", "내용1", List.of(1L), null, null));
        });

        Mockito.verify(appUserRepository, Mockito.times(1)).findByUserId(Mockito.anyLong());
        Mockito.verify(boardRepository, Mockito.times(1)).save(ArgumentMatchers.any(Board.class));
    }

    @Test
    @DisplayName("게시글 생성 성공")
    @Rollback
    void createBoardSuccess() {
        Mockito.when(appUserRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(boardRepository.save(Mockito.any()))
                .thenReturn(super.getBoard());
        Mockito.when(tagRepository.findAllByTagIdIn(Mockito.anyList()))
                .thenReturn(List.of(new Tag[]{ super.getTag() }));
        Mockito.when(boardTagRepository.saveAll(Mockito.any()))
                .thenReturn(null);

        Long boardId = boardService.create(new BoardDto.Request.Create("N", "제목1", "내용1", List.of(1L), null, null));

        System.out.println("Board ID: " + boardId);
        Assertions.assertNotNull(boardId);

        Mockito.verify(boardRepository, Mockito.times(1)).save(ArgumentMatchers.any(Board.class));
        Mockito.verify(boardTagRepository, Mockito.times(1)).saveAll(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("게시글 생성 및 썸네일 업로드 성공")
    @Rollback
    void createBoardSuccessWithThumbnail() {
        Mockito.when(fileStore.storeFile(Mockito.anyString(), Mockito.any(MultipartFile.class)))
                .thenReturn(FileDto.builder()
                        .storeFileName("test.jpg")
                        .originalFileName("test.jpg")
                        .storePath("test.jpg")
                        .size(1024L)
                        .extension("image/jpeg")
                        .build()
                );
        Mockito.when(appUserRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(boardRepository.save(Mockito.any()))
                .thenReturn(super.getBoard());
        Mockito.when(tagRepository.findAllByTagIdIn(Mockito.anyList()))
                .thenReturn(List.of(new Tag[]{ super.getTag() }));
        Mockito.when(boardTagRepository.saveAll(Mockito.any()))
                .thenReturn(null);

        Long boardId = boardService.create(new BoardDto.Request.Create("N", "제목1", "내용1", List.of(1L), super.getMockMultipartFile(), null));
        System.out.println("Board ID: " + boardId);

        Assertions.assertNotNull(boardId);
        Mockito.verify(fileStore, Mockito.times(1)).storeFile(Mockito.anyString(), Mockito.any(MultipartFile.class));
        Mockito.verify(boardRepository, Mockito.times(1)).save(ArgumentMatchers.any(Board.class));
    }

    @Test
    @DisplayName("게시글 생성 및 이미지 업로드 성공")
    @Rollback
    void createBoardSuccessWithImage() {
        Mockito.when(fileStore.storeFile(Mockito.anyString(), Mockito.any(MultipartFile.class)))
                .thenReturn(FileDto.builder()
                        .storeFileName("test.jpg")
                        .originalFileName("test.jpg")
                        .storePath("test.jpg")
                        .size(1024L)
                        .extension("image/jpeg")
                        .build()
                );
        Mockito.when(appUserRepository.findByUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(super.getAppUser()));
        Mockito.when(boardRepository.save(Mockito.any()))
                .thenReturn(super.getBoard());
        Mockito.when(tagRepository.findAllByTagIdIn(Mockito.anyList()))
                .thenReturn(List.of(new Tag[]{ super.getTag() }));
        Mockito.when(boardTagRepository.saveAll(Mockito.any()))
                .thenReturn(null);
        Mockito.when(boardImageRepository.findAllByImageIdInAndBoardIsNull(Mockito.anyList()))
                .thenReturn(List.of(new BoardImage[]{ super.getBoardImage() }));
        Mockito.when(boardImageRepository.saveAll(Mockito.any()))
                .thenReturn(null);
        Mockito.doNothing().when(boardImageRepository).deleteAllInBatch(Mockito.anyList());

        Long boardId = boardService.create(new BoardDto.Request.Create("N", "제목1", "내용1", List.of(1L), super.getMockMultipartFile(), List.of(1L)));
        System.out.println("Board ID: " + boardId);

        Assertions.assertNotNull(boardId);
        Mockito.verify(fileStore, Mockito.times(1)).storeFile(Mockito.anyString(), Mockito.any(MultipartFile.class));
        Mockito.verify(boardRepository, Mockito.times(1)).save(ArgumentMatchers.any(Board.class));
        Mockito.verify(boardImageRepository, Mockito.times(1)).deleteAllInBatch(Mockito.anyList());
    }
    
    @Test
    @DisplayName("게시글 가져오기 성공")
    void getBoardSuccess() {
        List<Board> board = List.of(new Board[]{ super.getBoard() });
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Board> returnBoards = new PageImpl<>(board, pageable, board.size());

        Mockito.when(boardRepository.findAllByCategoryOrderByCreatedTimeDesc(Mockito.any(), Mockito.anyChar()))
                .thenReturn(returnBoards);

        BoardDto.Response.Boards boards = boardService.getRecentPosts(1, 5, SortBoardTypeEnum.B);

        System.out.println("First Board Id: " + boards.getBoards().getFirst().getBoardId());
        System.out.println("First Board Title: " + boards.getBoards().getFirst().getTitle());

        Assertions.assertNotNull(boards);
        Assertions.assertNotNull(boards.getBoards());
        Assertions.assertEquals(1, boards.getBoards().size());
        Assertions.assertEquals(1L, boards.getTotal());
        Assertions.assertEquals(1L, boards.getBoards().getFirst().getBoardId());
        Assertions.assertEquals("title", boards.getBoards().getFirst().getTitle());
    }

    @Test
    @DisplayName("게시글 검색 성공")
    void getBoardSearchSuccess() {
        List<Board> board = List.of(new Board[]{ super.getBoard() });
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Board> returnBoards = new PageImpl<>(board, pageable, board.size());

        Mockito.when(boardRepository.findALlByTitleContainingOrderByCreatedTimeDesc(Mockito.any(), Mockito.anyString()))
                .thenReturn(returnBoards);

        BoardDto.Response.Boards boards = boardService.getSearchPosts(1, 5, "title");

        System.out.println("First Board Id: " + boards.getBoards().getFirst().getBoardId());
        System.out.println("First Board Title: " + boards.getBoards().getFirst().getTitle());

        Assertions.assertNotNull(boards);
        Assertions.assertNotNull(boards.getBoards());
        Assertions.assertEquals(1, boards.getBoards().size());
        Assertions.assertEquals(1L, boards.getTotal());
        Assertions.assertEquals(1L, boards.getBoards().getFirst().getBoardId());
        Assertions.assertEquals("title", boards.getBoards().getFirst().getTitle());
    }

    @Test
    @DisplayName("게시글 가져오기 성공 - 게시글 없음")
    void getBoardFailed() {
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Board> returnBoards = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(boardRepository.findAllByCategoryOrderByCreatedTimeDesc(Mockito.any(), Mockito.anyChar()))
                .thenReturn(returnBoards);

        BoardDto.Response.Boards boards = boardService.getRecentPosts(1, 5, SortBoardTypeEnum.B);

        Assertions.assertNotNull(boards);
        Assertions.assertNotNull(boards.getBoards());
        Assertions.assertEquals(0, boards.getBoards().size());
        Assertions.assertEquals(0L, boards.getTotal());
    }

    @Test
    @DisplayName("게시글 가져오기 성공 - 검색 결과 없음")
    void getBoardSearchFailed() {
        Pageable pageable = Pageable.ofSize(5).withPage(0);
        Page<Board> returnBoards = new PageImpl<>(List.of(), pageable, 0);

        Mockito.when(boardRepository.findALlByTitleContainingOrderByCreatedTimeDesc(Mockito.any(), Mockito.anyString()))
                .thenReturn(returnBoards);

        BoardDto.Response.Boards boards = boardService.getSearchPosts(1, 5, "title");

        Assertions.assertNotNull(boards);
        Assertions.assertNotNull(boards.getBoards());
        Assertions.assertEquals(0, boards.getBoards().size());
        Assertions.assertEquals(0L, boards.getTotal());
    }

    @Test
    @DisplayName("게시글 수정 가져오기 성공")
    void getUpdatePostSuccess() {
        Mockito.when(boardRepository.findByBoardId(Mockito.anyLong()))
                .thenReturn(Optional.of(super.getBoard()));

        BoardDto.Response.Update update = boardService.getUpdatePost(1L);

        System.out.println("Board Id: " + update.getBoardId());
        System.out.println("Board Title: " + update.getTitle());
        System.out.println("Board Content: " + update.getContent());
        System.out.println("Board Thumbnail: " + update.getThumbnail());
        System.out.println("Board Tags: " + update.getTags());
        System.out.println("Board Images: " + update.getImageIds());
        System.out.println("Board Created Time: " + update.getCreatedTime());

        Assertions.assertNotNull(update);
        Assertions.assertEquals(1L, update.getBoardId());
        Assertions.assertEquals("title", update.getTitle());
        Assertions.assertEquals("content", update.getContent());
        Assertions.assertEquals("test.jpg", update.getThumbnail());
        Assertions.assertEquals(0, update.getTags().size());
        Assertions.assertEquals(0, update.getImageIds().size());
        Assertions.assertNotNull(update.getCreatedTime());
    }

    @Test
    @DisplayName("게시글 수정 가져오기 실패 - 게시글 없음")
    void getUpdatePostFailedWithNotFound() {
        Mockito.when(boardRepository.findByBoardId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(CustomException.class, () -> {
            boardService.getUpdatePost(1L);
        });
    }

    @Test
    @DisplayName("게시글 상세 정보 가져오기 성공")
    void getPostDetail() {
        Mockito.when(boardRepository.findBoardIncludeUploaderByBoardId(ArgumentMatchers.anyLong())).thenReturn(Optional.of(super.getBoard()));

        BoardDto.Response.Detail detail = boardService.getPost(1L);

        System.out.println("Board Id: " + detail.getBoardId());
        System.out.println("Board Title: " + detail.getTitle());
        System.out.println("Board Uploader: " + detail.getUploader());
        System.out.println("Board Content: " + detail.getContent());
        System.out.println("Board Thumbnail: " + detail.getThumbnail());
        System.out.println("Board Tags: " + detail.getTags());
        System.out.println("Board Is Like: " + detail.getIsLike());
        System.out.println("Board Like Count: " + detail.getLikeCount());
        System.out.println("Board Comment Count: " + detail.getCommentCount());
        System.out.println("Board View Count: " + detail.getViewCount());
        System.out.println("Board Created Time: " + detail.getCreatedTime());

        Assertions.assertNotNull(detail);
        Assertions.assertEquals(1L, detail.getBoardId());
        Assertions.assertEquals("title", detail.getTitle());
        Assertions.assertNotNull(detail.getUploader());
        Assertions.assertEquals("appUser", detail.getUploader().getNickname());
        Assertions.assertEquals("content", detail.getContent());
        Assertions.assertEquals("test.jpg", detail.getThumbnail());
        Assertions.assertEquals(0, detail.getTags().size());
        Assertions.assertFalse(detail.getIsLike());
        Assertions.assertEquals(0, detail.getLikeCount());
        Assertions.assertEquals(0, detail.getCommentCount());
        Assertions.assertEquals(0, detail.getViewCount());
        Assertions.assertNotNull(detail.getCreatedTime());

        Mockito.verify(boardRepository, Mockito.times(1)).findBoardIncludeUploaderByBoardId(ArgumentMatchers.anyLong());
        Mockito.verify(boardTagRepository, Mockito.times(1)).findAllByBoardTagPK_Board(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("게시글 상세 정보 가져오기 실패 - 게시글 없음")
    void getPostDetailFailedWithNotFound() {
        Mockito.when(boardRepository.findBoardIncludeUploaderByBoardId(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(CustomException.class, () -> {
            boardService.getPost(1L);
        });

        Mockito.verify(boardRepository, Mockito.times(1)).findBoardIncludeUploaderByBoardId(ArgumentMatchers.anyLong());
    }
}
