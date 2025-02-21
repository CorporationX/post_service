package faang.school.postservice.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@Getter
public class AwsConfig {

    @Value("${services.s3.endpoint}")
    private String endpoint;

    @Value("${services.s3.accessKey}")
    private String accessKey;

    @Value("${services.s3.secretKey}")
    private String secretKey;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder();
        builder.credentialsProvider(StaticCredentialsProvider
                .create(AwsBasicCredentials.create(accessKey, secretKey)));

        builder.endpointOverride(URI.create(endpoint));

        S3Configuration confBuilder = S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build();

        builder.serviceConfiguration(confBuilder)
                .httpClient(UrlConnectionHttpClient.builder().build());

        S3Client client = builder
                .region(Region.of("ignored"))
                .build();

        return client;
    }
}
