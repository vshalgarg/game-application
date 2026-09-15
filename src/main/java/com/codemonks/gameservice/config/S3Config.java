package com.codemonks.gameservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

/**
 * Configures the AWS SDK's S3Client (for upload/delete operations) and
 * S3Presigner (for generating temporary signed download URLs).
 *
 * Works with both real AWS S3 and any S3-compatible provider (E2E Networks,
 * MinIO, etc.) as long as AwsS3Properties.endpoint is set for the latter.
 *
 * Note: S3Client's builder type is the standalone class S3ClientBuilder,
 * but S3Presigner's builder type is the NESTED interface S3Presigner.Builder
 * — the SDK is inconsistent between the two, this is not a typo.
 */
@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AwsS3Properties properties;

    /**
     * Shared credentials provider built once and reused by both beans below,
     * so the access-key/secret-key logic lives in exactly one place.
     */
    private StaticCredentialsProvider credentials() {
        return StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
        );
    }

    /**
     * True only when a custom endpoint (like E2E Networks) is configured.
     * Real AWS S3 doesn't need this — the SDK resolves its endpoint from region alone.
     */
    private boolean usingCustomEndpoint() {
        return properties.getEndpoint() != null && !properties.getEndpoint().isBlank();
    }

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(credentials());

        if (usingCustomEndpoint()) {
            builder
                    // Points the SDK at E2E Networks (or any custom provider)
                    // instead of the real AWS endpoint.
                    .endpointOverride(URI.create(properties.getEndpoint()))
                    // Non-AWS providers usually require "path-style" URLs
                    // (https://endpoint/bucket/key) instead of AWS's default
                    // "virtual-hosted style" (https://bucket.endpoint/key).
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build()
                    );
        }

        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        S3Presigner.Builder builder = S3Presigner.builder()
                .region(Region.of(properties.getRegion()))
                .credentialsProvider(credentials());

        if (usingCustomEndpoint()) {
            builder
                    .endpointOverride(URI.create(properties.getEndpoint()))
                    .serviceConfiguration(
                            S3Configuration.builder()
                                    .pathStyleAccessEnabled(true)
                                    .build()
                    );
        }

        return builder.build();
    }
}