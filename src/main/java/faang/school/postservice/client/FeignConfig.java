package faang.school.postservice.client;

import faang.school.postservice.config.context.UserContext;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
