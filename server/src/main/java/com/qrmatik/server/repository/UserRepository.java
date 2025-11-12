package com.qrmatik.server.repository;

import com.qrmatik.server.model.UserEntity;
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

    Optional<UserEntity> findTopByUsernameOrderByCreatedTimeDesc(String username);

    long countByTenant_Code(String tenantCode);
}
