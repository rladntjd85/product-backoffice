package com.rladntjd85.backoffice.common.security.util;

public class PasswordValidator {

    private PasswordValidator() {
    }

    public static void validatePassword(String raw) {
        // 최소 12자 + 대/소문자/숫자/특수문자 각 1개 이상
        if (raw == null || raw.length() < 12) {
            throw new IllegalArgumentException("비밀번호는 최소 12자 이상이어야 합니다.");
        }
        if (!raw.matches(".*[A-Z].*")) throw new IllegalArgumentException("비밀번호에 대문자가 1개 이상 필요합니다.");
        if (!raw.matches(".*[a-z].*")) throw new IllegalArgumentException("비밀번호에 소문자가 1개 이상 필요합니다.");
        if (!raw.matches(".*\\d.*")) throw new IllegalArgumentException("비밀번호에 숫자가 1개 이상 필요합니다.");
        if (!raw.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}:,.?].*")) {
            throw new IllegalArgumentException("비밀번호에 특수문자가 1개 이상 필요합니다.");
        }
    }
}
