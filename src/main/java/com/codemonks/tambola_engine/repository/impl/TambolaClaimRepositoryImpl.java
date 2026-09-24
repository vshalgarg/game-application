package com.codemonks.tambola_engine.repository.impl;

import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.repository.TambolaClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Repository("tambolaClaimRepositoryImpl")
@RequiredArgsConstructor
public class TambolaClaimRepositoryImpl implements TambolaClaimRepository {

    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    // >>> CHANGED: void -> Long. Pehle "Prefer: return=minimal" tha, isliye
    // >>> DB-generated claim_id (IDENTITY column) kabhi wapas nahi aata tha —
    // >>> caller (ClaimServiceImpl) fake epoch-millis ID use kar raha tha jo
    // >>> asli DB-row se match hi nahi karta tha. Ab "return=representation"
    // >>> se asli generated claim_id le rahe hain aur wapas kar rahe hain.
    @Override
    public Long insert(Claim claim) {
        String table = properties.getTables().getTambolaClaims();
        try {

            InsertClaim body = new InsertClaim(
                    claim.getRoomId(),
                    claim.getRoomCode(),
                    claim.getPlayerId(),
                    claim.getTicketId(),
                    claim.getRuleType().name(),
                    claim.getStatus().name()
            );

            List<InsertedClaimResult> inserted = tambolaSupabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=representation")   // >>> CHANGED
                    .body(List.of(body))
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<InsertedClaimResult>>() {});   // >>> CHANGED

            if (inserted == null || inserted.isEmpty()) {
                log.warn("[TAMBOLA_CLAIM_INSERT_EMPTY_RESPONSE] roomId={} playerId={}",
                        claim.getRoomId(), claim.getPlayerId());
                return null;
            }

            return inserted.get(0).claim_id();

        } catch (Exception e) {
            log.error("Failed to insert claim. roomId={} playerId={}", claim.getRoomId(), claim.getPlayerId(), e);
            throw new SupabaseStateException("Failed to insert claim for roomId=" + claim.getRoomId(), e);
        }
    }

    private record InsertClaim(
            Long room_id,
            String room_code,
            Long player_id,
            Long ticket_id,
            String rule_type,
            String status
    ) {}

    // >>> NAYA: PostgREST "return=representation" response se sirf
    // claim_id nikaalne ke liye.
    private record InsertedClaimResult(Long claim_id) {}
}