package com.codemonks.gameservice.repository;

import com.codemonks.gameservice.entity.RoomPlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomPlayerEntityRepository extends JpaRepository<RoomPlayerEntity, Long> {
    boolean existsByRoomIdAndUserId(Long id, String userId);
    int countByRoomId(Long id);
    Optional<RoomPlayerEntity> findByRoomIdAndUserId(Long id, String userId);
}