package faang.school.postservice.config.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@AllArgsConstructor
public class AwsConfig {
    private final AwsProperties awsProperties;

    @Bean
    public AmazonS3 amazonS3() {

        AWSCredentials awsCredentials = new BasicAWSCredentials(awsProperties.getUser(), awsProperties.getPassword());
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(awsProperties.getUrl(),
                        null))
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .enablePathStyleAccess()
                .build();

        if (!s3Client.doesBucketExistV2(awsProperties.getBucketName())) {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(awsProperties.getBucketName());
            Bucket bucket = s3Client.createBucket(createBucketRequest);
            log.info("Created bucket: {}", bucket.getName());
        }
        return s3Client;
    }
}
