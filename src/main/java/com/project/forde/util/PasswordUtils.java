package com.project.forde.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    // 비밀번호 암호화
    public static String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
    // 비밀번호 비교
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
