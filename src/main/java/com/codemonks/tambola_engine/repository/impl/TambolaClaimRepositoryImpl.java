package com.codemonks.tambola_engine.repository.impl;

import com.codemonks.tambola_engine.config.SupabaseProperties;
import com.codemonks.tambola_engine.domain.claim.Claim;
import com.codemonks.tambola_engine.exception.SupabaseStateException;
import com.codemonks.tambola_engine.repository.TambolaClaimRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Repository("tambolaClaimRepositoryImpl")
@RequiredArgsConstructor
public class TambolaClaimRepositoryImpl implements TambolaClaimRepository {

    private final RestClient tambolaSupabaseRestClient;
    private final SupabaseProperties properties;

    // Sirf insert — audit-record hai, kabhi update nahi hota, isliye version bhi nahi chahiye.
    @Override
    public void insert(Claim claim) {
        String table = properties.getTables().getTambolaClaims();
        try {

            InsertClaim body = new InsertClaim(
                    claim.getRoomId(),
                    claim.getPlayerId(),
                    claim.getTicketId(),
                    claim.getRuleType().name(),  // ✅ Convert Enum to String
                    claim.getStatus().name()     // ✅ Convert Enum to String
            );

            tambolaSupabaseRestClient.post()
                    .uri("/rest/v1/" + table)
                    .header("Prefer", "return=minimal")
                    .body(List.of(body))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Failed to insert claim. claimId={}", claim.getClaimId(), e);
            throw new SupabaseStateException("Failed to insert claim " + claim.getClaimId(), e);
        }
    }

    private record InsertClaim(
            Long room_id,
            Long player_id,
            Long ticket_id,
            String rule_type,
            String status
    ) {}
}