package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.request.ProfileRequestDTO;
import com.codemonks.gameservice.dto.response.ProfileResponseDTO;
import com.codemonks.gameservice.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiUrlConstants.Profile.BASE)
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> createOrUpdateProfile(@RequestBody ProfileRequestDTO request) {
        ProfileResponseDTO response = profileService.createOrUpdateProfile(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}