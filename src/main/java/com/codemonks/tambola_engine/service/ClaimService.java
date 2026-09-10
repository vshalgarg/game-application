package com.codemonks.tambola_engine.service;


import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;

// Poore claim-submission flow ka orchestrator - validation call karna,
// state update karna, timer ko WIN/FINISHED ke hisaab se signal dena -
// sab yahin coordinate hota hai.
public interface ClaimService {

    /**
     * @throws com.codemonks.tambola_engine.exception.InvalidClaimException
     *         agar claim kisi bhi wajah se reject ho (already claimed,
     *         pattern match nahi hua, invalid ticket/player, etc.)
     */
    ClaimResponseDTO submitClaim(ClaimRequestDTO request);
}