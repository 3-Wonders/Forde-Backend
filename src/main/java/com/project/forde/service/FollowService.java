package com.project.forde.service;


import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.entity.AppUser;
import com.project.forde.entity.Follow;
import com.project.forde.entity.Notification;
import com.project.forde.entity.composite.FollowPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.FollowMapper;
import com.project.forde.repository.FollowRepository;
import com.project.forde.repository.NotificationRepository;
import com.project.forde.type.NotificationTypeEnum;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final AppUserService appUserService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    /**
     * 팔로우 가능 여부를 확인합니다.
     * 이미 팔로우가 되어있다면 에러가 발생합니다.
     * 요청을 받은 사람이 팔로우 차단 설정을 하였다면 에러가 발생합니다.
     * @param followPK 팔로우 테이블을 검색하기 위한 복합 키
     * @param following 팔로우 요청을 받은 사람
     */
    public void checkFollowStatus(FollowPK followPK, AppUser following) {
        if(followRepository.findByFollowPK(followPK).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_FOLLOW);
        }

        if(following.getDisableFollow()) {
            throw new CustomException(ErrorCode.DISABLED_FOLLOWING);
        }
    }

    /**
     * 팔로잉 요청입니다.
     * follower : 팔로우 요청을 보낸 사람
     * following : 팔로우 요청을 받은 사람
     * @param receiverId 팔로우 요청을 한 사람의 유저 아이디
     */
    @UserVerify
    @Transactional
    public void requestFollowing(Long receiverId) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser follower = appUserService.getUser(userId);
        AppUser following = appUserService.getUser(receiverId);

        FollowPK followPK = FollowMapper.INSTANCE.toPK(following, follower);
        checkFollowStatus(followPK, following);

        notificationService.sendNotification(
                follower,
                following,
                NotificationTypeEnum.FOLLOW,
                null,
                null
        );
    }

    /**
     * 팔로잉 수락입니다.
     * 알림의 타입이 팔로잉에 관한 타입이 아니면 에러가 발생합니다.
     * @param notificationId 유효성 검증 및 알림 테이블에 있는 유저 아이디를 가져오기 위한 알림 아이디
     */
    @UserVerify
    public void acceptFollowRequest(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_NOTIFICATION));

        if(!notification.getNotificationType().equals("1008")) {
            throw new CustomException(ErrorCode.NOT_NOTIFICATION_TYPE_FOLLOW);
        }

        AppUser following = appUserService.getUser(notification.getReceiver().getUserId());
        AppUser follower = appUserService.getUser(notification.getSender().getUserId());

        FollowPK followPK = FollowMapper.INSTANCE.toPK(following, follower);

        checkFollowStatus(followPK, following);

        Follow follow = new Follow();
        follow.setFollowPK(followPK);

        followRepository.save(follow);
    }

    /**
     * 팔로잉 삭제입니다.
     * 팔로우가 되어있지 않은 사람을 삭제하려고 시도하면 에러가 발생합니다.
     * @param toUserId 삭제할 유저 아이디
     */
    @UserVerify
    public void removeFollowing(Long toUserId) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser follower = appUserService.getUser(userId);
        AppUser following = appUserService.getUser(toUserId);

        FollowPK followPK = FollowMapper.INSTANCE.toPK(following, follower);

        Follow follow = followRepository.findByFollowPK(followPK)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_FOLLOW));

        followRepository.delete(follow);
    }
}
