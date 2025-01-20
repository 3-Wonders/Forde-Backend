package com.project.forde;

import com.project.forde.entity.*;
import com.project.forde.entity.composite.BoardLikePK;
import com.project.forde.entity.composite.BoardTagPK;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;

@Slf4j
@ExtendWith({ MockitoExtension.class })
@Getter
public class AbstractTest {
    private final byte[] imageContent = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46, 0x00, 0x01};
    private final MockMultipartFile mockMultipartFile = new MockMultipartFile("thumbnail", "test.jpg", "image/jpeg", imageContent);;
    private final AppUser appUser = new AppUser();
    private final Board board = new Board();
    private final BoardLike boardLike = new BoardLike();
    private final Tag tag = Tag.builder()
            .tagName("tag")
            .build();
    private final BoardImage boardImage = BoardImage.builder()
            .imageSize(1024L)
            .imageType("image/jpeg")
            .imagePath("test.jpg")
            .build();

    @BeforeEach
    void setUp() {
        appUser.setUserId(1L);
        appUser.setUserPw("encryptedPassword");
        appUser.setEmail("appUser@example.com");
        appUser.setProfilePath("/images/profile/appUser.jpg");
        appUser.setProfileSize(2048L);
        appUser.setProfileType("image/jpeg");
        appUser.setNickname("appUser");
        appUser.setDescription("This is a test user.");
        appUser.setBoardCount(10L);
        appUser.setNewsCount(5L);
        appUser.setCommentCount(20L);
        appUser.setLikeCount(50L);
        appUser.setFollowerCount(100L);
        appUser.setFollowingCount(150L);
        appUser.setDisableFollow(false);
        appUser.setPrivateAccount(false);
        appUser.setNoticeNotification(true);
        appUser.setCommentNotification(true);
        appUser.setLikeNotification(true);
        appUser.setRecommendNotification(true);
        appUser.setFollowNotification(true);
        appUser.setEventNotification(true);
        appUser.setDeleted(false);
        appUser.setDeletedTime(null);
        appUser.setCreatedTime(LocalDateTime.now());

        board.setBoardId(1L);
        board.setCategory('N');
        board.setTitle("title");
        board.setContent("content");
        board.setThumbnailPath("test.jpg");
        board.setThumbnailSize(1024L);
        board.setThumbnailType("image/jpeg");
        board.setUploader(appUser);
        board.setLikeCount(0);
        board.setCommentCount(0);
        board.setViewCount(0);
        board.setIsLike(false);
        board.setCreatedTime(LocalDateTime.now());

        boardLike.setBoardLikePK(new BoardLikePK(appUser, board));
        boardLike.setCreatedTime(LocalDateTime.now());

        tag.setTagCount(0L);
    }
}
