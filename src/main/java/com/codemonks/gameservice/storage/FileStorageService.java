package com.codemonks.gameservice.storage;

public interface FileStorageService {

    String generateAccessUrl(String objectKey);

    void uploadFile(String objectKey, byte[] content, String contentType);

    void deleteFile(String objectKey);
}
