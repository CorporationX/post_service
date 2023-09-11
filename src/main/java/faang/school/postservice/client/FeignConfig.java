package faang.school.postservice.client;

import faang.school.postservice.config.context.ProjectContext;
import faang.school.postservice.config.context.UserContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }

    @Bean
    public FeignProjectInterceptor feignProjectInterceptor(ProjectContext projectContext) {
        return new FeignProjectInterceptor(projectContext);
    }
}
