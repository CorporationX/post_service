package faang.school.postservice.service.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceServiceConfiguration {
    @Value("${resource.post-resource-bucket}")
    private String resourceBucketName;

    @Bean("post-resource-bucket")
    public String resourceBucket() {
        return resourceBucketName;
    }
}
