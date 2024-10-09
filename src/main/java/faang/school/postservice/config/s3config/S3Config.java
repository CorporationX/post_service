package faang.school.postservice.config.s3config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import faang.school.postservice.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final AppProperties appProperties;

    @Bean
    public AmazonS3 s3client() {
        AWSStaticCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(appProperties.getAccessKey(), appProperties.getSecretKey()));
        var endpointConfig = new AwsClientBuilder.EndpointConfiguration(appProperties.getEndpoint(), "");
        var clientConfig = new ClientConfiguration()
                .withConnectionTimeout(appProperties.getConnectionTimeout())
                .withSocketTimeout(appProperties.getSocketTimeout())
                .withProtocol(Protocol.HTTPS);

        return AmazonS3Client.builder()
                .withCredentials(credentialsProvider)
                .withEndpointConfiguration(endpointConfig)
                .withClientConfiguration(clientConfig)
                .enablePathStyleAccess()
                .build();
    }
}
