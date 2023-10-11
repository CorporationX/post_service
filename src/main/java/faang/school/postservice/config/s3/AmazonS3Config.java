package faang.school.postservice.config.s3;

import com.amazonaws.SdkBaseException;
import com.amazonaws.SdkClientException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;

import java.net.URI;

@Configuration
@Getter
public class AmazonS3Config {
    @Value("${aws.accessKey}")
    private String accessKey;
    @Value("${aws.secretKey}")
    private String secretKey;
    @Value("${aws.region}")
    private String region;
    @Value("${services.s3.bucketName}")
    private String bucketName;
    @Bean
    public S3Client s3Client() {
        try {
            S3ClientBuilder builder = S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey));

            if (bucketName != null && !bucketName.isEmpty()) {
                builder.endpointOverride(URI.create("https://" + bucketName + ".s3.amazonaws.com"));
            }

            return builder.build();
        }
        catch (SdkClientException e) {
            e.printStackTrace();
            return null;
        }
    }
}