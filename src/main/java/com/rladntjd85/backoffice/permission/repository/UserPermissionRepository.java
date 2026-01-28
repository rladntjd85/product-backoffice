package com.rladntjd85.backoffice.permission.repository;

import com.rladntjd85.backoffice.permission.domain.UserPermission;
import com.rladntjd85.backoffice.permission.domain.UserPermissionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UserPermissionId> {
}
