package com.codemonks.ludo_engine.service.Impl;

import com.codemonks.ludo_engine.config.SupabaseProperties;
import com.codemonks.ludo_engine.service.BotRoomLockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.Map;

// Distributed lock — Postgres (Supabase) row-based, safe across multiple
// horizontally-scaled instances behind a load balancer. Replaces the
// previous in-memory ConcurrentHashMap implementation.

@Slf4j
@Service
@RequiredArgsConstructor
public class BotRoomLockServiceImpl implements BotRoomLockService {

    private static final long STALE_LOCK_MINUTES = 2;

    private final RestClient supabaseRestClient;
    private final SupabaseProperties properties;

    @Override
    public boolean tryAcquire(Long roomId) {

        clearStaleLock(roomId);

        String table = properties.getTables().getBotRoomLocks();

        try {
            supabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=minimal")
                    .body(Map.of(
                            "room_id", roomId,
                            "locked_at", OffsetDateTime.now().toString()
                    ))
                    .retrieve()
                    .toBodilessEntity();

            log.info("[BOT_LOCK_ACQUIRED] Room:{}", roomId);
            return true;

        } catch (HttpClientErrorException.Conflict conflict) {
            log.warn("[BOT_LOCK_SKIPPED] Room:{} — a bot loop is already active, skipping duplicate trigger", roomId);
            return false;

        } catch (Exception e) {
            log.error("[BOT_LOCK_ACQUIRE_FAILED] Room:{}", roomId, e);
            return false;
        }
    }

    @Override
    public void release(Long roomId) {
        String table = properties.getTables().getBotRoomLocks();

        try {
            supabaseRestClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .build())
                    .header("Prefer", "return=minimal")
                    .retrieve()
                    .toBodilessEntity();

            log.info("[BOT_LOCK_RELEASED] Room:{}", roomId);

        } catch (Exception e) {
            log.error("[BOT_LOCK_RELEASE_FAILED] Room:{}", roomId, e);
        }
    }

    private void clearStaleLock(Long roomId) {
        String table = properties.getTables().getBotRoomLocks();

        try {
            OffsetDateTime cutoff = OffsetDateTime.now().minusMinutes(STALE_LOCK_MINUTES);

            supabaseRestClient.delete()
                    .uri(uriBuilder -> uriBuilder
                            .path("/rest/v1/" + table)
                            .queryParam("room_id", "eq." + roomId)
                            .queryParam("locked_at", "lt." + cutoff)
                            .build())
                    .header("Prefer", "return=minimal")
                    .retrieve()
                    .toBodilessEntity();

        } catch (Exception e) {
            log.warn("[BOT_LOCK_STALE_CLEANUP_FAILED] Room:{}", roomId, e);
        }
    }
}