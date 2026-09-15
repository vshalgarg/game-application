package com.codemonks.gameservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Binds all object-storage config values from application.yml (under the
 * "e2e.s3" prefix) into a single typed object, instead of scattering
 * @Value("${...}") annotations across multiple classes.
 *
 * Class/field names still say "S3" because E2E Networks' Object Store is
 * S3-COMPATIBLE (same API shape as AWS S3) — the AWS SDK itself is being
 * pointed at E2E's endpoint instead of the real AWS endpoint. This is normal:
 * "S3 API" and "AWS's S3 service" are not the same thing.
 */
@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "e2e.s3")
public class AwsS3Properties {

    /** Region string, e.g. "ap-south-1". SDK requires this even for non-AWS providers. */
    private String region;

    /** Bucket name where all sound files (and other assets) get stored. */
    private String bucket;

    /** Access key for E2E's object store. Must come from an env variable, never hardcoded. */
    private String accessKey;

    /** Secret key for E2E's object store. Must come from an env variable, never hardcoded. */
    private String secretKey;

    /**
     * Custom endpoint URL of the storage provider, e.g.
     * "https://objectstore.e2enetworks.net" for E2E Networks.
     * This is what tells the AWS SDK "don't go to real AWS, go here instead."
     */
    private String endpoint;

    /** How many hours a generated presigned URL stays valid before it expires. */
    private long presignedUrlExpiryHours;
}