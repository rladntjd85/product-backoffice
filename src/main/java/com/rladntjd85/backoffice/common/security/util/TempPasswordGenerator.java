package com.rladntjd85.backoffice.common.security.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TempPasswordGenerator {

    private static final SecureRandom RND = new SecureRandom();

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnopqrstuvwxyz";
    private static final String DIGIT = "23456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}:,.?";

    private static final String ALL = UPPER + LOWER + DIGIT + SPECIAL;

    public String generate() {
        int len = 18;

        List<Character> chars = new ArrayList<>(len);

        // 최소 1개씩 보장
        chars.add(pick(UPPER));
        chars.add(pick(LOWER));
        chars.add(pick(DIGIT));
        chars.add(pick(SPECIAL));

        while (chars.size() < len) {
            chars.add(pick(ALL));
        }

        Collections.shuffle(chars, RND);

        StringBuilder sb = new StringBuilder(len);
        for (char c : chars) sb.append(c);
        return sb.toString();
    }

    private char pick(String s) {
        return s.charAt(RND.nextInt(s.length()));
    }
}
