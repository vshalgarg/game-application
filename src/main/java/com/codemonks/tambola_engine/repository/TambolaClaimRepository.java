package com.codemonks.tambola_engine.repository;


import com.codemonks.tambola_engine.domain.claim.Claim;

public interface TambolaClaimRepository {
    Long insert(Claim claim);
}