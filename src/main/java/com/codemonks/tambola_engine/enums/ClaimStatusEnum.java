package com.codemonks.tambola_engine.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClaimStatusEnum {
    PENDING,
    APPROVED,
    REJECTED
}