package com.codemonks.tambola_engine.service;


import com.codemonks.tambola_engine.dto.request.ClaimRequestDTO;
import com.codemonks.tambola_engine.dto.response.ClaimResponseDTO;

public interface ClaimService {

    ClaimResponseDTO submitClaim(ClaimRequestDTO request);
}