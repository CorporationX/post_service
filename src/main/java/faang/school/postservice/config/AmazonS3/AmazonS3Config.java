package faang.school.postservice.config.AmazonS3;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AmazonS3Config {

    @Value("${spring.services.s3.accessKey}")
    private String accessKey;

    @Value("${spring.services.s3.secretKey}")
    private String secretKey;

    @Value("${spring.services.s3.bucketName}")
    private String bucketName;

    @Value("${spring.services.s3.endpoint}")
    private String endpoint;

    @Bean
    public AmazonS3 s3Client() {
        AmazonS3 s3Client = AmazonS3ClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint,
                        Regions.US_EAST_1.getName()))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                        accessKey,
                        secretKey
                )))
                .withPathStyleAccessEnabled(true)
                .build();

        if (!s3Client.doesBucketExistV2(bucketName)) {
            s3Client.createBucket(new CreateBucketRequest(bucketName));
        }

        return s3Client;
    }
}
