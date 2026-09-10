package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.context.UserContext;
import com.codemonks.gameservice.dto.request.ProfileRequestDTO;
import com.codemonks.gameservice.dto.response.ProfileResponseDTO;
import com.codemonks.gameservice.entity.ProfileEntity;
import com.codemonks.gameservice.repository.ProfileEntityRepository;
import com.codemonks.gameservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileEntityRepository profileRepository;

    @Override
    @Transactional
    public ProfileResponseDTO createOrUpdateProfile(ProfileRequestDTO request) {
        ProfileEntity profile = profileRepository.findByUserId(request.getUserId()).orElseGet(ProfileEntity::new);
        profile.setUserId(request.getUserId());
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        if (request.getDob() != null) {
            profile.setDob(request.getDob());
        }
        profile = profileRepository.save(profile);
        return buildProfileResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResponseDTO getProfile() {
        Long userId = UserContext.getUserId();
        ProfileEntity profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            return ProfileResponseDTO.builder()
                    .userId(userId)
                    .name(null)
                    .email(null)
                    .dob(null)
                    .profileStatus(false)
                    .build();
        }
        return buildProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(ProfileRequestDTO request) {
        Long userId = UserContext.getUserId();
        ProfileEntity profile = profileRepository.findByUserId(userId)
                        .orElseGet(() -> ProfileEntity.builder().userId(userId).build());
        profile.setName(request.getName());
        profile.setDob(request.getDob());
        profile = profileRepository.save(profile);
        return buildProfileResponse(profile);
    }

    private ProfileResponseDTO buildProfileResponse(ProfileEntity profile) {
        boolean profileStatus = profile.getName() != null
                        && !profile.getName().isBlank()
                        && profile.getDob() != null;

        return ProfileResponseDTO.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .email(profile.getEmail())
                .dob(profile.getDob())
                .profileStatus(profileStatus)
                .build();
    }
}