package faang.school.postservice.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.AwsHostNameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3ClientConfig {
    @Value("${client.s3.minio.username}")
    private String minioUsername;

    @Value("${client.s3.minio.password}")
    private String minioPassword;

    @Value("${client.s3.minio.url}")
    private String minioUrl;

    @Bean(name = "minioS3Client")
    public AmazonS3 minioS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(minioUsername, minioPassword);

        return AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                minioUrl,
                                AwsHostNameUtils.parseRegion(minioUrl, AmazonS3Client.S3_SERVICE_NAME)
                        )
                )
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
