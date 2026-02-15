package com.rladntjd85.backoffice.auth.service;

import com.rladntjd85.backoffice.common.security.util.PasswordValidator;
import com.rladntjd85.backoffice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changePassword(String email, String newPassword) {

        PasswordValidator.validatePassword(newPassword);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        user.updatePasswordHash(passwordEncoder.encode(newPassword));
        user.markMustChangePassword(false);
        user.resetFailedLoginCount();
        user.unlock();
    }

    @Transactional
    public void changePasswordWithCurrent(String email, String currentPassword, String newPassword) {
        PasswordValidator.validatePassword(newPassword);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("현재 비밀번호를 입력하세요.");
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("현재 비밀번호가 올바르지 않습니다.");
        }

        user.updatePasswordHash(passwordEncoder.encode(newPassword));
        user.markMustChangePassword(false);
        user.resetFailedLoginCount();
        user.unlock();
    }

}
