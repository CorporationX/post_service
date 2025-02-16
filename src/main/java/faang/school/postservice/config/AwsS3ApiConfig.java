package faang.school.postservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Getter
@Setter
public class AwsS3ApiConfig {
    //@Value("${AWS_S3_ENDPOINT}")
    private String endpoint;

    //@Value("${AWS_ACCESS_KEY}")
    private String accessKey;

    //@Value("${AWS_SECRET_KEY}")
    private String secretKey;

    //@Value("${AWS_S3_BUCKET}")
    private String bucket;

    //@Value("${AWS_REGION}")
    private String region;

    //@Value("${AWS_S3_MAX_CONCURRENCY:50}")
    private int maxConcurrency;

    //@Value("${AWS_S3_CONNECTION_TIMEOUT:PT30S}")
    private Duration connectionTimeout;

    //@Value("${AWS_S3_READ_TIMEOUT:PT30S}")
    private Duration readTimeout;

    //@Value("${AWS_S3_WRITE_TIMEOUT:PT30S}")
    private Duration writeTimeout;

    //@Value("${AWS_S3_API_CALL_TIMEOUT:PT30S}")
    private Duration apiCallTimeout;

    //@Value("${AWS_S3_API_CALL_ATTEMPT_TIMEOUT:PT30S}")
    private Duration apiCallAttemptTimeout;

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(endpoint))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(URI.create(endpoint))
                .build();
    }
}