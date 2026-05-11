package com.feasymenu.server.repository;

import com.feasymenu.server.model.CustomerRewardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRewardRepository extends JpaRepository<CustomerRewardEntity, UUID> {
    Optional<CustomerRewardEntity> findByRewardCode(String rewardCode);

    List<CustomerRewardEntity> findByEmail(String email);

    java.util.List<CustomerRewardEntity> findBySessionId(String sessionId);
}
