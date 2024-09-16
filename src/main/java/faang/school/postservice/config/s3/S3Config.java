package faang.school.postservice.config.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

@Configuration
public class S3Config {
    @Value("${spring.aws.s3.accessKey}")
    private String accessKey;
    @Value("${spring.aws.s3.secretKey}")
    private String secretKey;
    @Value("${spring.aws.s3.region}")
    private String region;
    @Value("${spring.aws.s3.endpoint}")
    private String endpoint;

    @Bean
    public AmazonS3 S3Client() {

        var s3ClientBuilder = AmazonS3ClientBuilder.standard();

        if (!Objects.equals(endpoint, "default")) {
            s3ClientBuilder.withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration(endpoint, region)
            );
        }

        s3ClientBuilder
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withPathStyleAccessEnabled(true)
        ;

        return s3ClientBuilder.build();
    }
}
