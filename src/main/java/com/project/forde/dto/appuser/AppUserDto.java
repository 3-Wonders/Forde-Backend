package com.project.forde.dto.appuser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.project.forde.dto.sns.SnsDto;
import com.project.forde.dto.tag.TagDto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class AppUserDto {

    public static class Request {

        @Getter
        public static class Signup {
            @NotBlank(message = "이메일을 작성해주세요.")
            @Size(max = 60, message = "이메일은 60자 이하여야 합니다.")
            private String email;

            @NotBlank(message = "비밀번호를 작성해주세요.")
            @Size(min = 8, max = 20, message = "비밀번호는 8 ~ 20자 이내로 입력해야 합니다.")
            @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", message = "영문자, 숫자, 특수문자를 1개씩 포함해야 합니다.")
            private String password;

            @NotNull(message = "일반 알림 여부를 체크해주세요.")
            private Boolean isEnableNotification;

            @NotNull(message = "이벤트성 알림 여부를 체크해주세요.")
            private Boolean isEnableEvent;
        }

        @Getter
        public static class UpdateSocialSetting {
            @NotNull(message = "팔로우 차단 여부를 체크해주세요.")
            private Boolean disableFollow;
            @NotNull(message = "비공개 계정 여부를 체크해주세요.")
            private Boolean disableAccount;
        }

        @Getter
        public static class UpdateNotificationSetting {
            @NotNull(message = "공지 알림 여부를 체크해주세요.")
            private Boolean noticeNotification;
            @NotNull(message = "댓글 알림 여부를 체크해주세요.")
            private Boolean commentNotification;
            @NotNull(message = "좋아요 알림 여부를 체크해주세요.")
            private Boolean likeNotification;
            @NotNull(message = "추천 뉴스/게시글 알림 여부를 체크해주세요.")
            private Boolean recommendNotification;
            @NotNull(message = "팔로우한 사람의 뉴스/게시글 알림 여부를 체크해주세요.")
            private Boolean followNotification;
            @NotNull(message = "이벤트 알림 여부를 체크해주세요.")
            private Boolean eventNotification;
        }

        @Getter
        public static class UpdateMyInfo {
            @Size(max = 10, message = "닉네임은 10자 이하여야 합니다.")
            private String nickname;
            private String description;
            private List<Long> interestTags;
        }

        @Getter
        public static class UpdateProfileImage {
            private final MultipartFile image;

            @JsonCreator
            public UpdateProfileImage(MultipartFile image) {
                this.image = image;
            }
        }
    }

    public static class Response {

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Intro {
            private Long userId;
            private String nickname;
            private String email;
            private String profilePath;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class LightInfo {
            private String profilePath;
            private String nickname;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class MyInfo {
            private Long userId;
            private String nickname;
            private String description;
            private String profilePath;
            private List<TagDto.Response.TagWithoutCount> interestedTags;
            private Integer boardCount;
            private Integer newsCount;
            private Integer likeCount;
            private Integer commentCount;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class Account {
            private Long userId;
            private String email;
            private List<SnsDto.Response.connectedStatus> snsInfos;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        public static class SearchUserNickname {
            private Long userId;
            private String nickname;
            private String profilePath;
        }
    }
}
