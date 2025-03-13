package com.project.forde.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.project.forde.annotation.ExtractUserId;
import com.project.forde.annotation.UserVerify;
import com.project.forde.aspect.ExtractUserIdAspect;
import com.project.forde.aspect.UserVerifyAspect;
import com.project.forde.dto.FileDto;
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
import com.project.forde.exception.FileUploadException;
import com.project.forde.mapper.*;
import com.project.forde.repository.AppUserRepository;
import com.project.forde.type.AppUserCount;
import com.project.forde.type.BoardTypeEnum;
import com.project.forde.repository.*;
import com.project.forde.type.SocialTypeEnum;
import com.project.forde.util.FileStore;
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
    private final BoardTagRepository boardTagRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final RedisStore redisStore;
    private final FileStore fileStore;

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
        AppUser appUser = appUserRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(appUser.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        if(!appUser.getVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_USER);
        }

        return appUser;
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

    /**
     * 사용자와 관련된 board 를 매핑하여 DTO 로 변환하여 반환합니다.
     * @param boards 반환하고자 하는 board 목록(페이지네이션 적용)
     * @return 명세서에 정의된 DTO 형식
     */
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

    /**
     * 검색하고자 하는 사용자 및 카테고리에 대한 board 를 검색한 후 반환해주는 서비스 내의 메소드를 호출합니다.
     * @param userId 검색하고자 하는 유저의 아이디
     * @param type 검색하고자 하는 카테고리(뉴스, 질문, 게시물)
     * @param page 현재 페이지 수
     * @param count 한 페이지에 검색할 board 갯수
     * @return 명세서에 정의된 DTO 형식
     */
    public BoardDto.Response.UserBoards getUserBoard(Long userId, Character type, final int page, final int count) {
        AppUser appUser = getUser(userId);

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<Board> boards = boardRepository.findAllByUploaderAndCategoryOrderByCreatedTimeDesc(pageable, appUser, type);

        return createUserBoardsDto(boards);
    }

    /**
     * 검색하고자 하는 사용자가 좋아요한 게시물을 반환합니다.
     * @param userId 검색하고자 하는 유저의 아이디
     * @param page 현재 페이지 수
     * @param count 한 페이지에 검색할 board 갯수
     * @return 명세서에 정의된 DTO 형식
     */
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

    /**
     * 검색하고자 하는 사용자가 작성한 댓글을 반환합니다.
     * @param userId 검색하고자 하는 유저의 아이디
     * @param page 현재 페이지 수
     * @param count 한 페이지에 검색할 board 및 댓글 갯수
     * @return 명세서에 정의된 DTO 형식
     */
    public BoardDto.Response.UserComments getUserBoardsWithComments(Long userId, final int page, final int count) {
        AppUser appUser = getUser(userId);

        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<Comment> comments = commentRepository.findAllByUploaderOrderByCreatedTimeDesc(appUser, pageable);

        List<BoardDto.Response.UserComments.UserComment> mappingBoards = comments.stream().map(comment -> {
            Board board = boardRepository.findByBoardId(comment.getBoard().getBoardId())
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_BOARD));

            List<BoardTag> boardTags = boardTagRepository.findAllByBoardTagPK_Board(board);
            List<Tag> tags = boardTags.stream().map(boardTag -> boardTag.getBoardTagPK().getTag()).toList();
            List<TagDto.Response.TagWithoutCount> responseTags = tags.stream().map(TagMapper.INSTANCE::toTagWithoutCount).toList();

            return BoardMapper.INSTANCE.toUserCommentInBoards(board, responseTags, comment);
        }).toList();

        return new BoardDto.Response.UserComments(mappingBoards, comments.getTotalElements());
    }

    /**
     * 현재 사용자의 간단한 정보를 반환합니다.
     * @return 명세서에 정의된 DTO 형식
     */
    @UserVerify
    public AppUserDto.Response.Intro getIntroUser() {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        return AppUserMapper.INSTANCE.toResponseIntroUserDto(appUser);
    }

    @UserVerify
    public AppUserDto.Response.MySnsInfo getMySnsInfo() {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        return AppUserMapper.INSTANCE.toResponseSnsDto(appUser);
    }

    @UserVerify
    public AppUserDto.Response.MyNotificationInfo getMyNotificationInfo() {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        return AppUserMapper.INSTANCE.toResponseMyNotificationInfoDto(appUser);
    }

    /**
     * 현재 사용자의 정보를 반환합니다.
     * @return 명세서에 정의된 DTO 형식
     */
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

    /**
     * 현재 사용자의 계정 정보를 반환합니다.
     * @return 명세서에 정의된 DTO 형식
     */
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

    /**
     * 댓글 작성 시, 멘션할 사용자를 검색하는 서비스입니다.
     * @param page 현재 페이지 수
     * @param count 한 페이지에 검색할 사용자 수
     * @param nickname 사용자가 검색한 닉네임
     * @return 명세서에 정의된 DTO 형식
     */
    public List<AppUserDto.Response.SearchUserNickname> getSearchUserNickname(final int page, final int count, String nickname) {
        Pageable pageable = Pageable.ofSize(count).withPage(page - 1);

        Page<AppUser> appUsers = appUserRepository.findAllByNicknameContaining(pageable, nickname);

        return appUsers.stream().map(AppUserMapper.INSTANCE::toResponseSearchNicknameDto).toList();
    }

    /**
     * 사용자의 정보를 수정합니다.
     * 닉네임은 10자 이하여야 하며, 초과할 시 에러가 발생합니다.
     * @param dto (닉네임, 자기소개, 관심있는 태그)
     */
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

    /**
     * 회원가입을 수행합니다.
     * 1. 이메일 형식을 가지지 못하면 에러가 발생합니다.
     * 2. 이미 존재하는 계정이라면 에러가 발생합니다.
     * 3. 비밀번호는 8 ~ 20자 이내이어야 하며, 영문자 및 특수문자가 1개씩 포함되지 않으면 에러가 발생합니다.
     * 4. 일반 알림 여부 및 이벤트성 알림 여부를 체크하지 않으면(true, false 설정 가능) 에러가 발생합니다.
     * 5. 닉네임은 User_7글자의 랜덤 문자열로 자동 생성됩니다.
     * @param dto (이메일, 비밀번호, 일반 알림 여부, 이벤트성 알림 여부)
     */
    public void createAppUser(AppUserDto.Request.Signup dto) {
        Optional<AppUser> appUser = appUserRepository.findByEmailAndUserPwIsNotNull(dto.getEmail());

        if (appUser.isPresent()) { // 이메일 중복
            throw new CustomException(ErrorCode.DUPLICATED_ACCOUNT);
        }

        AppUser newUser = AppUserMapper.INSTANCE.toEntity(dto);
        newUser.setUserPw(PasswordUtils.encodePassword(dto.getPassword()));

        String name;
        do {
            name = "User_" + RandomStringUtils.random(5, 48, 122, true, true);
        } while (appUserRepository.findByNickname(name).isPresent());
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

    /**
     * Forde 계정의 로그인을 수행합니다.
     * 1. 이메일 및 비밀번호가 일치하지 않으면 에러가 발생합니다.
     * 2. 이메일 인증이 완료된 사용자가 아니면 에러가 발생합니다.
     * 3. 삭제된 사용자라면 에러가 발생합니다.
     * @param dto (이메일, 패스워드)
     * @return 유저 아이디
     */
    public Long login(RequestLoginDto dto) {
        AppUser user = appUserRepository.findByEmailAndUserPwIsNotNull(dto.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(user.getDeleted()) {
            throw new CustomException(ErrorCode.DELETED_USER);
        }

        if(!user.getVerified()) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_USER);
        }

        if(!PasswordUtils.checkPassword(dto.getPassword(), user.getUserPw())) {
            throw new CustomException(ErrorCode.NOT_MATCHED_LOGIN_INFO);
        }

        return user.getUserId();
    }

    /**
     * SNS 사용자의 회원가입을 수행합니다.
     * 1. 닉네임은 User_7글자의 랜덤 문자열로 자동 생성됩니다.
     * 2. 이미 존재하는 이메일이 있을 경우, 에러가 발생합니다.
     * 3. 만약 Provider 에게 이메일을 받지 못하면 null로 설정합니다.
     * @param email Provider 로부터 받은 이메일
     * @param profilePath Provider 로부터 받은 프로필 사진 주소
     * @return 생성된 유저 정보
     */
    public AppUser createSnsUser(String email, String profilePath) {
        AppUser newAppUser = new AppUser();
        String name;

        do {
            name = "User_" + RandomStringUtils.random(5, 48, 122, true, true);
        } while (appUserRepository.findByNickname(name).isPresent());

        newAppUser.setEmail(email);
        if(email != null) newAppUser.setVerified(true);
        newAppUser.setNickname(name);
        newAppUser.setProfilePath(profilePath);
        appUserRepository.save(newAppUser);
        return newAppUser;
    }

    /**
     * 사용자의 이메일 인증 여부를 true로 설정합니다.
     * @param email 사용자 이메일
     * @return 사용자 아이디
     */
    @ExtractUserId
    public Long setUserVerify(String email) {
        Long userId = ExtractUserIdAspect.getUserId();
        AppUser appUser = appUserRepository.findByUserId(userId).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if(appUser.getEmail() == null) { // 이메일이 존재하지 않을 경우 -> SNS 로그인 과정에서 이메일을 넘겨받지 못한 경우 이메일 설정
            appUser.setEmail(email);
        }

        if(appUser.getEmail() != null) { // 이메일이 존재할 경우(자체 회원가입일 경우) -> 회원이 존재하지 않을 경우 에러 발생
            appUserRepository.findByEmailAndUserPwIsNotNull(email)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        }

        appUser.setVerified(true);

        appUserRepository.save(appUser);

        return appUser.getUserId();
    }

    /**
     * 사용자의 비밀번호를 수정합니다.
     * @param passWord 변경하고자 하는 비밀번호
     */
    @UserVerify
    public void updatePassword(String passWord) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        if(appUser.getUserPw() == null) {
            throw new CustomException(ErrorCode.CAN_NOT_USE_SNS_USER);
        }

        appUser.setUserPw(PasswordUtils.encodePassword(passWord));

        appUserRepository.save(appUser);

        redisStore.deleteField("email:randomKey:" + userId, "randomKeyValue");
    }

    /**
     * 사용자의 이메일을 변경합니다.
     * @param userId 변경하고자 하는 유저 아이디
     * @param email 변경하고자 하는 이메일
     */
    public void updateEmail(Long userId, String email) {
        AppUser updateUser = getUser(userId);

        updateUser.setEmail(email);
        updateUser.setVerified(true);
        appUserRepository.save(updateUser);

        redisStore.deleteField("email:verification:" + email, "verificationCode");
    }

    /**
     * 현재 사용자의 유저 아이디를 가져와 이메일 변경 메소드를 호출합니다.
     * @param email 변경하고자 하는 이메일
     */
    @UserVerify
    public void updateUserEmail(String email) {
        Long userId = UserVerifyAspect.getUserId();
        updateEmail(userId, email);
    }

    /**
     * 사용자 소셜 설정을 수정합니다.
     * @param dto (팔로우 차단 여부, 비공개 계정 여부)
     */
    @UserVerify
    public void updateSocialSetting(AppUserDto.Request.UpdateSocialSetting dto) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        appUser.setDisableFollow(dto.getDisableFollow());
        appUser.setPrivateAccount(dto.getDisableAccount());

        appUserRepository.save(appUser);
    }

    /**
     * 사용자 알림 정보를 수정합니다.
     * @param dto (공지, 댓글, 좋아요, 추천 뉴스 / 게시물, 팔로우한 사람의 뉴스 / 게시물, 이벤트 알림 여부)
     */
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

    /**
     * 사용자의 프로필 이미지를 변경합니다.
     * @param dto 프로필 이미지가 담긴 dto
     */
    @UserVerify
    public void updateProfileImage(AppUserDto.Request.UpdateProfileImage dto) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        FileDto file = null;
        try {
            file = fileStore.storeFile("profile/" + userId, dto.getImage());
            fileStore.deleteFile(appUser.getProfilePath());

            appUser.setProfilePath(file.getStorePath());
            appUser.setProfileSize(file.getSize());
            appUser.setProfileType(file.getExtension());
            
            appUserRepository.save(appUser);
        }
        catch(Exception e) {
            if (file != null) {
                throw new FileUploadException(file.getStorePath());
            }
        }
    }

    /**
     * 현재 사용자의 계정을 삭제합니다(Soft Delete이기 때문에 완전 삭제는 아닙니다)
     * @param request 현재 사용자의 세션 정보를 얻기 위한 request
     */
    @UserVerify
    public void removeUser(HttpServletRequest request) {
        Long userId = UserVerifyAspect.getUserId();

        AppUser appUser = getUser(userId);

        appUser.setDeleted(true);
        appUserRepository.save(appUser);
        request.getSession().invalidate();
    }
}
