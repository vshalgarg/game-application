package com.codemonks.gameservice.service;

import com.codemonks.gameservice.dto.request.ProfileRequestDTO;
import com.codemonks.gameservice.dto.request.SocialProfileRequestDTO;
import com.codemonks.gameservice.dto.response.ProfileResponseDTO;

public interface ProfileService {
    ProfileResponseDTO createOrUpdateProfile(SocialProfileRequestDTO request);
    ProfileResponseDTO getProfile();
    ProfileResponseDTO updateProfile(ProfileRequestDTO request);
}