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
}
