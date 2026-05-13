package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerEntityRepository extends JpaRepository<PlayerEntity, Long> {
    int countByRoom_Id(Long roomCode);
    boolean existsByRoom_IdAndUserId(Long id, Long userId);
    Optional<PlayerEntity> findByRoom_IdAndUserId(Long id, Long userId);
    List<PlayerEntity> findByRoom_Id(Long id);
}