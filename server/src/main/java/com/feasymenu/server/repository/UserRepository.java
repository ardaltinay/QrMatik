package com.feasymenu.server.repository;

import com.feasymenu.server.model.UserEntity;
import com.feasymenu.server.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);

    List<UserEntity> findByTenant_Code(String tenantCode);

    Optional<UserEntity> findByUsernameAndTenant_Code(String username, String tenantCode);

    Optional<UserEntity> findTopByUsernameAndTenant_CodeOrderByCreatedTimeDesc(String username, String tenantCode);

    Optional<UserEntity> findTopByUsernameAndTenantIsNullOrderByCreatedTimeDesc(String username);

    Optional<UserEntity> findTopByUsernameOrderByCreatedTimeDesc(String username);

    long countByTenant_Code(String tenantCode);

    List<UserEntity> findByRole(UserRole role);

    Optional<UserEntity> findByUsernameAndTenantCode(String username, String tenantCode);
}
