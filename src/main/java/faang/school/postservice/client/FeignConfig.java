package faang.school.postservice.client;

import faang.school.postservice.config.context.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class FeignConfig {
    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        log.debug("FeignUserInterceptor created");
        return new FeignUserInterceptor(userContext);
    }
}
