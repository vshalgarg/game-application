package com.codemonks.gameservice.service.impl;

import com.codemonks.gameservice.constants.ResponseErrorCodes;
import com.codemonks.gameservice.dto.response.SoundEventResponseDTO;
import com.codemonks.gameservice.dto.response.SoundListResponseDTO;
import com.codemonks.gameservice.entity.SoundEventEntity;
import com.codemonks.gameservice.enums.GameTypeEnum;
import com.codemonks.gameservice.exceptions.GameException;
import com.codemonks.gameservice.exceptions.ResourceNotFoundException;
import com.codemonks.gameservice.repository.SoundEventEntityRepository;
import com.codemonks.gameservice.service.SoundService;
import com.codemonks.gameservice.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SoundServiceImpl implements SoundService {

    private final SoundEventEntityRepository soundEventEntityRepository;
    private final FileStorageService fileStorageService;

    @Override
    public SoundListResponseDTO getList(String gameType, String theme) {

        GameTypeEnum parsedGameType = parseGameType(gameType);
        String resolvedTheme = (theme == null || theme.isBlank())
                ? "DEFAULT"
                : theme.toUpperCase();

        List<SoundEventEntity> soundEvents = soundEventEntityRepository
                .findByGameTypeAndThemeKeyAndIsActiveTrue(parsedGameType, resolvedTheme);

        Map<String, SoundListResponseDTO.SoundEventResponseDTO> events = soundEvents.stream()
                .collect(Collectors.toMap(
                        SoundEventEntity::getEventKey,
                        event -> SoundListResponseDTO.SoundEventResponseDTO.builder()
                                .url(fileStorageService.generateAccessUrl(event.getS3ObjectKey()))
                                .version(event.getVersion())
                                .build()
                ));

        log.info(
                "[SOUND_MANIFEST_BUILT] gameType={}, theme={}, eventCount={}",
                parsedGameType, resolvedTheme, events.size()
        );

        return SoundListResponseDTO.builder()
                .gameType(parsedGameType.name())
                .theme(resolvedTheme)
                .events(events)
                .build();
    }

    @Override
    public SoundEventResponseDTO uploadSound(
            String gameType,
            String themeKey,
            String eventKey,
            MultipartFile file
    ) {
        GameTypeEnum parsedGameType = parseGameType(gameType);
        String resolvedTheme = (themeKey == null || themeKey.isBlank())
                ? "DEFAULT"
                : themeKey.toUpperCase();
        String resolvedEventKey = eventKey.toUpperCase();

        boolean alreadyExists = soundEventEntityRepository
                .findByGameTypeAndThemeKeyAndIsActiveTrue(parsedGameType, resolvedTheme)
                .stream()
                .anyMatch(e -> e.getEventKey().equals(resolvedEventKey));

        if (alreadyExists) {
            throw new GameException(
                    ResponseErrorCodes.SOUND_ALREADY_EXISTS,
                    "Sound already exists for gameType=" + parsedGameType +
                            ", theme=" + resolvedTheme + ", eventKey=" + resolvedEventKey +
                            ". Use update instead."
            );
        }

        String format = extractFormat(file.getOriginalFilename());
        String objectKey = buildObjectKey(parsedGameType, resolvedTheme, resolvedEventKey, format);

        uploadToStorage(objectKey, file);

        SoundEventEntity entity = SoundEventEntity.builder()
                .gameType(parsedGameType)
                .themeKey(resolvedTheme)
                .eventKey(resolvedEventKey)
                .s3ObjectKey(objectKey)
                .format(format)
                .version(1L)
                .isActive(true)
                .build();

        SoundEventEntity saved = soundEventEntityRepository.save(entity);

        log.info("[SOUND_UPLOADED] id={}, objectKey={}", saved.getId(), objectKey);

        return toResponseDTO(saved);
    }

    @Override
    public SoundEventResponseDTO updateSound(Long id, MultipartFile file, Boolean isActive) {

        SoundEventEntity entity = soundEventEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseErrorCodes.SOUND_NOT_FOUND));

        if (file != null && !file.isEmpty()) {
            uploadToStorage(entity.getS3ObjectKey(), file);
            entity.setVersion(entity.getVersion() + 1);
            log.info("[SOUND_FILE_REPLACED] id={}, newVersion={}", id, entity.getVersion());
        }

        if (isActive != null) {
            entity.setIsActive(isActive);
        }

        SoundEventEntity saved = soundEventEntityRepository.save(entity);
        return toResponseDTO(saved);
    }

    @Override
    public void deleteSound(Long id) {

        SoundEventEntity entity = soundEventEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseErrorCodes.SOUND_NOT_FOUND));

        fileStorageService.deleteFile(entity.getS3ObjectKey());
        soundEventEntityRepository.delete(entity);

        log.info("[SOUND_DELETED] id={}, objectKey={}", id, entity.getS3ObjectKey());
    }

    private void uploadToStorage(String objectKey, MultipartFile file) {
        try {
            fileStorageService.uploadFile(objectKey, file.getBytes(), file.getContentType());
        } catch (IOException e) {
            throw new GameException(
                    ResponseErrorCodes.SOUND_FILE_READ_FAILED,
                    "Failed to read uploaded file: " + e.getMessage()
            );
        }
    }

    private String buildObjectKey(GameTypeEnum gameType, String theme, String eventKey, String format) {
        return gameType.name().toLowerCase() + "/" +
                theme.toLowerCase() + "/" +
                eventKey.toLowerCase() + "." + format;
    }

    private String extractFormat(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "mp3";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
    }

    private GameTypeEnum parseGameType(String gameType) {
        try {
            return GameTypeEnum.valueOf(gameType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new GameException(
                    ResponseErrorCodes.INVALID_GAME_TYPE,
                    "Invalid gameType: " + gameType
            );
        }
    }

    private SoundEventResponseDTO toResponseDTO(SoundEventEntity entity) {
        return SoundEventResponseDTO.builder()
                .id(entity.getId())
                .gameType(entity.getGameType().name())
                .themeKey(entity.getThemeKey())
                .eventKey(entity.getEventKey())
                .url(fileStorageService.generateAccessUrl(entity.getS3ObjectKey()))
                .format(entity.getFormat())
                .version(entity.getVersion())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}