package com.codemonks.gameservice.service;

import com.codemonks.gameservice.dto.response.SoundEventResponseDTO;
import com.codemonks.gameservice.dto.response.SoundListResponseDTO;
import org.springframework.web.multipart.MultipartFile;

public interface SoundService {

    SoundListResponseDTO getList(String gameType, String theme);

    SoundEventResponseDTO uploadSound(
            String gameType,
            String themeKey,
            String eventKey,
            MultipartFile file
    );

    SoundEventResponseDTO updateSound(
            Long id,
            MultipartFile file,
            Boolean isActive
    );

    void deleteSound(Long id);
}