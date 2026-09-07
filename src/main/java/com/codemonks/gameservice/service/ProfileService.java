package com.codemonks.gameservice.service;

import com.codemonks.gameservice.dto.request.ProfileRequestDTO;
import com.codemonks.gameservice.dto.response.ProfileResponseDTO;

public interface ProfileService {

    ProfileResponseDTO createOrUpdateProfile(
            ProfileRequestDTO request
    );
}