package faang.school.postservice.client;

import faang.school.postservice.config.context.UserContext;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }
}
