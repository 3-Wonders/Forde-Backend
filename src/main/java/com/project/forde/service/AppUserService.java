package com.project.forde.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.RequestLoginDto;
import com.project.forde.dto.ResponseOtherUserDto;
import com.project.forde.dto.appuser.AppUserDto;
import com.project.forde.dto.board.BoardDto;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import com.project.forde.entity.*;
import com.project.forde.entity.composite.InterestTagPK;
import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import com.project.forde.mapper.AppUserMapper;
import com.project.forde.mapper.BoardMapper;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.type.AppUserCount;
import com.project.forde.type.BoardTypeEnum;
import com.project.forde.mapper.InterestTagMapper;
import com.project.forde.mapper.TagMapper;
import com.project.forde.repository.*;
import com.project.forde.type.SocialTypeEnum;
import com.project.forde.util.PasswordUtils;
import com.project.forde.util.RedisStore;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final InterestTagRepository interestTagRepository;
    private final TagRepository tagRepository;
    private final SnsRepository snsRepository;
    private final BoardRepository boardRepository;
    private final MailService mailService;
    private final BoardTagRepository boardTagRepository;
    private final LikeRepository likeRepository;
    private final RedisStore redisStore;

    public ResponseOtherUserDto getOtherUser(Long userId) {
        AppUser user = this.getUser(userId);
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
    public AppUser getUser(Long userId) {
        return appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
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

    public BoardDto.Response.UserBoards createUserBoardsDto(Page<Board> boards) {
        List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_BoardIn(boards.toList());
        ListMultimap<Long, Tag> tagMap = ArrayListMultimap.create();

        boardTags.forEach(boardTag -> {
            Tag tag = boardTag.getBoardTagPK().getTag();
            tagMap.put(boardTag.getBoardTagPK().getBoard().getBoardId(), tag);
        });

        List<BoardDto.Response.UserBoards.UserBoard> mappingBoards = boards.getContent().stream().map(board -> {
            List<Tag> tags = tagMap.get(board.getBoardId());
            List<TagDto.Response.TagWithoutCount> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

            return BoardMapper.INSTANCE.toUserBoardsInBoard(board, responseTags);
        }).toList();

        return new BoardDto.Response.UserBoards(mappingBoards, boards.getTotalElements());
    }

    public BoardDto.Response.UserBoards getUserBoard(Long userId, Character type, final int page, final int count) {
        AppUser appUser = getUser(userId);

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<Board> boards = boardRepository.findAllByUploaderAndCategoryOrderByCreatedTimeDesc(pageable, appUser, type);

        return createUserBoardsDto(boards);
    }

    public BoardDto.Response.UserBoards getUserLikeBoard(Long userId, final int page, final int count) {
        AppUser appUser = getUser(userId);

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        List<BoardLike> boardLikes = likeRepository.findAllByBoardLikePK_User(appUser);
        List<Long> boardIds = boardLikes.stream()
                .map(boardLike -> boardLike.getBoardLikePK().getBoard().getBoardId())
                .toList();

        Page<Board> boards = boardRepository.findAllByBoardIdInOrderByCreatedTimeDesc(boardIds, pageable);

        return createUserBoardsDto(boards);
    }



    @UserVerify
    public AppUserDto.Response.Intro getIntroUser() {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        return AppUserMapper.INSTANCE.toResponseIntroUserDto(appUser);
    }

    @UserVerify
    public AppUserDto.Response.MyInfo getMyInfo() {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        List<InterestTag> interestTags = interestTagRepository.findAllById_AppUser(appUser);
        List<Long> interestTagIds = interestTags.stream()
                .map(interestTag -> interestTag.getId().getTag().getTagId())
                .collect(Collectors.toList());
        List<Tag> tags = tagRepository.findAllByTagIdIn(interestTagIds);
        List<TagDto.Response.TagWithoutCount> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

        return AppUserMapper.INSTANCE.toResponseMyInfoDto(appUser, responseTags);
    }

    @UserVerify
    public AppUserDto.Response.Account getAccount() {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        List<Sns> sns = snsRepository.findAllByAppUser(appUser);

        Set<String> snsKindsWithTrue = sns.stream()
                .map(Sns::getSnsKind)
                .collect(Collectors.toSet());

        List<SnsDto.Response.connectedStatus> snsInfos = Arrays.asList("1001", "1002", "1003", "1004").stream()
                .map(snsKind -> new SnsDto.Response.connectedStatus(
                        Integer.valueOf(snsKind),
                        SocialTypeEnum.fromSnsKind(snsKind).name(),
                        snsKindsWithTrue.contains(snsKind)
                ))
                .toList();

        return AppUserMapper.INSTANCE.toResponseAccountDto(appUser, snsInfos);

    }

    public List<AppUserDto.Response.SearchUserNickname> getSearchUserNickname(final int page, final int count, String nickname) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<AppUser> appUsers = appUserRepository.findAllByNicknameContaining(pageable, nickname);

        return appUsers.stream().map(AppUserMapper.INSTANCE::toResponseSearchNicknameDto).toList();
    }

    @UserVerify
    public void updateMyInfo(AppUserDto.Request.UpdateMyInfo dto) {
        Long userId = UserVerifyAspect.getUserId();
        AppUser appUser = getUser(userId);

        appUser.setNickname(dto.getNickname());
        appUser.setDescription(dto.getDescription());
        appUserRepository.save(appUser);

        List<Tag> tags = tagRepository.findAllByTagIdIn(dto.getInterestTags());

        List<InterestTag> userInterestTag = interestTagRepository.findAllById_AppUser(appUser);
        interestTagRepository.deleteAll(userInterestTag);
        List<InterestTag> newInterestTag = new ArrayList<>();

        for (Tag tag : tags) {
            InterestTagPK interestTagPK = InterestTagMapper.INSTANCE.toPK(tag, appUser);

            InterestTag interestTag = new InterestTag();
            interestTag.setId(interestTagPK);

            newInterestTag.add(interestTag);
        }

        interestTagRepository.saveAll(newInterestTag);
    }

    public void createAppUser(AppUserDto.Request.Signup dto) {
        Optional<AppUser> appUser = appUserRepository.findByEmail(dto.getEmail());

        if (appUser.isPresent()) { // 이메일 중복
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }

        AppUser newUser = AppUserMapper.INSTANCE.toEntity(dto);
        newUser.setUserPw(PasswordUtils.encodePassword(dto.getPassword()));

        String name;
        do {
            name = "User_" + RandomStringUtils.random(7, 48, 122, true, true);
        } while (appUserRepository.findByEmail(name).isPresent());
        newUser.setNickname(name);

        if(dto.getIsEnableNotification()) {
            newUser.setRecommendNotification(true);
            newUser.setNoticeNotification(true);
            newUser.setCommentNotification(true);
            newUser.setLikeNotification(true);
            newUser.setFollowNotification(true);
        }

        if(dto.getIsEnableEvent()) {
            newUser.setEventNotification(true);
        }

        appUserRepository.save(newUser);
    }

    public Long login(RequestLoginDto dto) {
        AppUser user = appUserRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        if(!PasswordUtils.checkPassword(dto.getPassword(), user.getUserPw())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_LOGIN_INFO);
        }

        if(!user.getVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_USER);
        }

        return user.getUserId();
    }

    public AppUser createSnsUser(String email,String profilePath) {
        AppUser newAppUser = new AppUser();
        String name;

        do {
            name = "User_" + RandomStringUtils.random(7, 48, 122, true, true);
        } while (appUserRepository.findByEmail(name).isPresent());

        newAppUser.setEmail(email);
        if(email != null) newAppUser.setVerified(true);
        newAppUser.setNickname(name);
        newAppUser.setProfilePath(profilePath);
        appUserRepository.save(newAppUser);
        return newAppUser;
    }

    @UserVerify
    public void updatePassword(AppUserDto.Request.UpdatePassword dto) {
        Long userId = UserVerifyAspect.getUserId();
        mailService.verifyRandomKey(dto.getRandomKey());

        AppUser appUser = getUser(userId);

        appUser.setUserPw(PasswordUtils.encodePassword(dto.getPassword()));

        appUserRepository.save(appUser);

        redisStore.deleteField("email:randomKey:" + userId, "randomKeyValue");
    }

    @UserVerify
    public void updateSocialSetting(AppUserDto.Request.UpdateSocialSetting dto) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        appUser.setDisableFollow(dto.getDisableFollow());
        appUser.setPrivateAccount(dto.getDisableAccount());

        appUserRepository.save(appUser);
    }

    @UserVerify
    public void updateNotificationSetting(AppUserDto.Request.UpdateNotificationSetting dto) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        appUser.setNoticeNotification(dto.getNoticeNotification());
        appUser.setCommentNotification(dto.getCommentNotification());
        appUser.setLikeNotification(dto.getLikeNotification());
        appUser.setFollowNotification(dto.getFollowNotification());
        appUser.setRecommendNotification(dto.getRecommendNotification());
        appUser.setEventNotification(dto.getEventNotification());
        appUserRepository.save(appUser);
    }

    @UserVerify
    public void removeUser(HttpServletRequest request) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        appUser.setDeleted(true);
        appUserRepository.save(appUser);
        request.getSession().invalidate();
    }
}
