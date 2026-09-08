package com.codemonks.gameservice.service.impl;

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
public class ProfileServiceImpl implements ProfileService { private final ProfileEntityRepository profileRepository;
    @Override
    @Transactional
    public ProfileResponseDTO createOrUpdateProfile(ProfileRequestDTO request)
    {
        ProfileEntity profile = profileRepository.findByUserId(request.getUserId()).orElseGet(ProfileEntity::new);
        profile.setUserId(request.getUserId());
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile = profileRepository.save(profile);
        return ProfileResponseDTO.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .email(profile.getEmail())
                .build();
    }
}