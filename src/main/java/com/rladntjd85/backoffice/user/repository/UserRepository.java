package com.rladntjd85.backoffice.user.repository;

import com.rladntjd85.backoffice.auth.domain.Role;
import com.rladntjd85.backoffice.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    Page<User> findByEnabled(boolean enabled, Pageable pageable);
    Page<User> findByLockedTrue(Pageable pageable);

    long countByRole(Role role);
}
