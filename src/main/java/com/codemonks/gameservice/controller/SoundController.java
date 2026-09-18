package com.codemonks.gameservice.controller;

import com.codemonks.gameservice.constants.ApiUrlConstants;
import com.codemonks.gameservice.dto.ApiResponse;
import com.codemonks.gameservice.dto.response.SoundEventResponseDTO;
import com.codemonks.gameservice.dto.response.SoundListResponseDTO;
import com.codemonks.gameservice.service.SoundService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.codemonks.gameservice.constants.ApiUrlConstants.Sound.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiUrlConstants.Sound.BASE)
@Slf4j
public class SoundController {

    private final SoundService soundService;

    @GetMapping(GET_LIST)
    public ResponseEntity<ApiResponse<SoundListResponseDTO>> getList(
            @RequestParam String gameType,
            @RequestParam(defaultValue = "DEFAULT") String theme
    ) {
        log.info("Sound list request received. gameType={}, theme={}", gameType, theme);

        SoundListResponseDTO response = soundService.getList(gameType, theme);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<SoundEventResponseDTO>> uploadSound(
            @RequestParam String gameType,
            @RequestParam(required = false) String themeKey,
            @RequestParam String eventKey,
            @RequestPart MultipartFile file
    ) {
        log.info("Sound upload request. gameType={}, eventKey={}", gameType, eventKey);

        SoundEventResponseDTO response = soundService.uploadSound(gameType, themeKey, eventKey, file);
        return ResponseEntity.ok(ApiResponse.success(response, "Sound uploaded successfully"));
    }

    @PutMapping(value = BY_ID, consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<SoundEventResponseDTO>> updateSound(
            @PathVariable Long id,
            @RequestPart(required = false) MultipartFile file,
            @RequestParam(required = false) Boolean isActive
    ) {
        log.info("Sound update request. id={}", id);

        SoundEventResponseDTO response = soundService.updateSound(id, file, isActive);
        return ResponseEntity.ok(ApiResponse.success(response, "Sound updated successfully"));
    }

    @DeleteMapping(BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteSound(@PathVariable Long id) {

        log.info("Sound delete request. id={}", id);

        soundService.deleteSound(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Sound deleted successfully"));
    }
}