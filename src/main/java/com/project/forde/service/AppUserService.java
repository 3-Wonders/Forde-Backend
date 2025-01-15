package com.project.forde.service;

import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.entity.AppUser;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.AppUserMapper;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.type.AppUserCount;
import com.project.forde.type.BoardTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;

    public ResponseOtherUserDto getOtherUser(Long userId) {
        AppUser user = this.verifyUserAndGet(userId);
        return AppUserMapper.INSTANCE.toResponseOtherUserDto(user);
    }

    /**
     * 사용자의 정보를 검증하고, 사용자 객체를 반환합니다.
     * <br /> <br />
     * 사용자가 존재하지 않으면 예외를 발생시킵니다.
     * 사용자가 이메일 인증을 하지 않았다면 예외를 발생시킵니다.
     * 사용자가 삭제되었다면 예외를 발생시킵니다.
     *
     * @param userId 사용자 ID
     * @throws CustomException 사용자가 존재하지 않을 경우
     * @return 사용자 정보
     */
    public AppUser verifyUserAndGet(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        // TODO : 사용자가 이메일 인증을 하지 않았다면 예외를 발생시킵니다.
        if (user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        return user;
    }

    /**
     * 게시글 관련 작성 횟수를 증가시킵니다.
     *
     * @param user 사용자 객체
     * @param boardType 게시판 타입
     */
    public void increaseCount(AppUser user, BoardTypeEnum boardType) {
        if (boardType.equals(BoardTypeEnum.B)) {
            user.setBoardCount(user.getBoardCount() + 1);
        } else if (boardType.equals(BoardTypeEnum.N)) {
            user.setNewsCount(user.getNewsCount() + 1);
        } else {
            user.setQuestionCount(user.getQuestionCount() + 1);
        }

        appUserRepository.save(user);
    }

    /**
     * 게시글 관련 작성 횟수를 감소시킵니다.
     *
     * @param user 사용자 객체
     * @param boardType 게시판 타입
     */
    public void decreaseCount(AppUser user, BoardTypeEnum boardType) {
        if (boardType.equals(BoardTypeEnum.B)) {
            user.setBoardCount(user.getBoardCount() - 1);
        } else if (boardType.equals(BoardTypeEnum.N)) {
            user.setNewsCount(user.getNewsCount() - 1);
        } else {
            user.setQuestionCount(user.getQuestionCount() - 1);
        }

        appUserRepository.save(user);
    }

    /**
     * 사용자 관련 카운트를 증가시킵니다. (댓글, 좋아요, 팔로잉, 팔로워)
     *
     * @param user      사용자 객체
     * @param countType 카운트 타입
     */
    public void increaseCount(AppUser user, AppUserCount countType) {
        switch (countType) {
            case COMMENT_COUNT:
                user.setCommentCount(user.getCommentCount() + 1);
                break;
            case LIKE_COUNT:
                user.setLikeCount(user.getLikeCount() + 1);
                break;
            case FOLLOWING_COUNT:
                user.setFollowingCount(user.getFollowingCount() + 1);
                break;
            case FOLLOWER_COUNT:
                user.setFollowerCount(user.getFollowerCount() + 1);
                break;
        }

        appUserRepository.save(user);

    }

    /**
     * 사용자 관련 카운트를 감소시킵니다. (댓글, 좋아요, 팔로잉, 팔로워)
     *
     * @param user 사용자 객체
     * @param countType 카운트 타입
     */
    public void decreaseCount(AppUser user, AppUserCount countType) {
        switch (countType) {
            case COMMENT_COUNT:
                user.setCommentCount(user.getCommentCount() - 1);
                break;
            case LIKE_COUNT:
                user.setLikeCount(user.getLikeCount() - 1);
                break;
            case FOLLOWING_COUNT:
                user.setFollowingCount(user.getFollowingCount() - 1);
                break;
            case FOLLOWER_COUNT:
                user.setFollowerCount(user.getFollowerCount() - 1);
                break;
        }

        appUserRepository.save(user);
    }
}
