package com.project.forde.type;

import com.project.forde.exception.CustomException;
import com.project.forde.exception.ErrorCode;
import lombok.Getter;

@Getter
public enum SocialTypeEnum {
    KAKAO("1001"),
    NAVER("1002"),
    GOOGLE("1003"),
    GITHUB("1004");

    private final String snsKind;

    SocialTypeEnum(String snsKind) {
        this.snsKind = snsKind;
    }

    public static SocialTypeEnum fromSnsKind(String snsKind) {
        for (SocialTypeEnum type : values()) {
            if (type.getSnsKind().equals(snsKind)) {
                return type;
            }
        }
        throw new CustomException(ErrorCode.INVALID_SOCIAL_TYPE);
    }
}
