package com.project.forde.type;

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
}
