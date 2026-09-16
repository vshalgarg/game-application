package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.context.UserContext;
import com.codemonks.gameservice.dto.request.ProfileRequestDTO;
import com.codemonks.gameservice.dto.request.SocialProfileRequestDTO;
import com.codemonks.gameservice.dto.response.ProfileResponseDTO;
import com.codemonks.gameservice.entity.ProfileEntity;
import com.codemonks.gameservice.repository.ProfileEntityRepository;
import com.codemonks.gameservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileEntityRepository profileRepository;

    @Override
    @Transactional
    public ProfileResponseDTO createOrUpdateProfile(SocialProfileRequestDTO request) {
        ProfileEntity profile = profileRepository.findByUserId(request.getUserId()).orElseGet(ProfileEntity::new);
        profile.setUserId(request.getUserId());
        profile.setFirstName(request.getName());
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
                    .displayName(null)
                    .firstName(null)
                    .lastName(null)
                    .email(null)
                    .dob(null)
                    .gender(null)
                    .countryCode(null)
                    .avatarId(null)
                    .phoneNumber(null)
                    .profileStatus(false)
                    .build();
        }
        return buildProfileResponse(profile);
    }

    @Override
    @Transactional
    public ProfileResponseDTO updateProfile(ProfileRequestDTO request) {
        Long userId = UserContext.getUserId();
        ProfileEntity profile = profileRepository
                        .findByUserId(userId)
                        .orElseGet(() -> ProfileEntity.builder().userId(userId).build());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setDob(request.getDob());
        profile.setGender(request.getGender());
        profile.setCountryCode(request.getCountryCode());
        profile.setAvatarId(request.getAvatarId());
        profile.setEmail(request.getEmail());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile = profileRepository.save(profile);
        return buildProfileResponse(profile);
    }

    private ProfileResponseDTO buildProfileResponse(ProfileEntity profile) {
        String displayName = Stream.of(profile.getFirstName(), profile.getLastName())
                        .filter(Objects::nonNull)
                        .filter(value -> !value.isBlank())
                        .collect(Collectors.joining(" "));
        boolean profileStatus = profile.getFirstName() != null && !profile.getFirstName().isBlank()
                && profile.getLastName() != null
                && !profile.getLastName().isBlank()
                && profile.getDob() != null
                && profile.getGender() != null
                && profile.getCountryCode() != null
                && !profile.getCountryCode().isBlank()
                && profile.getAvatarId() != null
                && profile.getPhoneNumber() != null
                && !profile.getPhoneNumber().isBlank();


        return ProfileResponseDTO.builder()
                .userId(profile.getUserId())
                .displayName(displayName.isBlank() ? null : displayName)
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .email(profile.getEmail())
                .dob(profile.getDob())
                .gender(profile.getGender())
                .countryCode(profile.getCountryCode())
                .avatarId(profile.getAvatarId())
                .phoneNumber(profile.getPhoneNumber())
                .profileStatus(profileStatus)
                .build();
    }

}