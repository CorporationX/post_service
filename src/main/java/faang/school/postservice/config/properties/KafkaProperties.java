package faang.school.postservice.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {

    @NotBlank
    private String bootstrapServers;

    @Valid
    @NotNull
    private Topic topic;

    @Valid
    @NotNull
    private Producer producer;

    @Valid
    @NotNull
    private Consumer consumer;

    @Getter
    @Setter
    public static class Topic {

        @NotBlank
        private String posts;

        @NotBlank
        private String postViews;

        @NotBlank
        private String likes;

        @NotBlank
        private String comments;

        @NotNull
        @Min(1)
        private Integer partitions;

        @NotNull
        @Min(1)
        private Short replicas;

        @NotBlank
        @Pattern(regexp = "\\d+", message = "retentionMs must be a number in string format")
        private String retentionMs;

        @NotBlank
        @Pattern(regexp = "delete|compact|delete,compact", message = "Invalid cleanup policy")
        private String cleanupPolicy;

        @NotBlank
        @Pattern(regexp = "\\d+", message = "minInSyncReplicas must be a number in string format")
        private String minInSyncReplicas;

        @NotBlank
        @Pattern(regexp = "true|false", message = "uncleanLeaderElection must be 'true' or 'false'")
        private String uncleanLeaderElection;
    }

    @Getter
    @Setter
    public static class Producer {

        @NotBlank
        @Pattern(regexp = "all|0|1", message = "acks must be 'all', '0', or '1'")
        private String acks;

        @NotNull
        private Boolean idempotence;

        @NotNull
        @Min(0)
        private Integer retries;

        @NotNull
        @Min(1)
        @Max(5)
        private Integer maxInFlightRequests;

        @NotBlank
        @Pattern(regexp = "zstd|gzip|lz4|snappy|uncompressed", message = "Invalid compression type")
        private String compressionType;
    }

    @Getter
    @Setter
    public static class Consumer {

        @NotBlank
        private String groupId;

        @NotNull
        private Boolean enableAutoCommit;

        @NotBlank
        @Pattern(regexp = "earliest|latest|none", message = "autoOffsetReset must be one of: earliest, latest, none")
        private String autoOffsetReset;
    }
}
