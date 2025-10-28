package com.qrmatik.server.repository;

import com.qrmatik.server.model.UserEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByUsername(String username);
    java.util.List<com.qrmatik.server.model.UserEntity> findByTenant_Code(String tenantCode);
    Optional<com.qrmatik.server.model.UserEntity> findByUsernameAndTenant_Code(String username, String tenantCode);
}
