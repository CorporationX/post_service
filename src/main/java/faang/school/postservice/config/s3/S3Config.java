package faang.school.postservice.config.s3;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@EnableConfigurationProperties({S3Properties.class, AwsRegionProperties.class})
@Configuration
public class S3Config {

    private final S3Properties props;
    private final AwsRegionProperties regionProps;

    public S3Config(S3Properties props, AwsRegionProperties regionProps) {
        this.props = props;
        this.regionProps = regionProps;
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .endpointOverride(URI.create(props.getEndpoint()))
                .region(Region.of(regionProps.getStaticRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        props.getAccessKey(), props.getSecretKey())))
                .build();
    }
}
