package faang.school.postservice.config.s3;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@RequiredArgsConstructor
public class S3Config {
    private final S3Properties props;

    @Bean
    public AmazonS3 amazonS3() {
        BasicAWSCredentials creds =
                new BasicAWSCredentials(props.getAccessKey(), props.getSecretKey());
        AwsClientBuilder.EndpointConfiguration endpoint =
                new AwsClientBuilder.EndpointConfiguration(props.getEndpoint(), props.getRegion());

        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(endpoint)
                .withPathStyleAccessEnabled(props.isPathStyle())
                .withClientConfiguration(new ClientConfiguration().withSignerOverride("AWSS3V4SignerType"))
                .withCredentials(new AWSStaticCredentialsProvider(creds))
                .build();

        if (!s3.doesBucketExistV2(props.getBucket())) {
            s3.createBucket(props.getBucket());
        }

        return s3;
    }
}
