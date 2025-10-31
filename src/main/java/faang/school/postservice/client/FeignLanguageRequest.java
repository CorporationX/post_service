package faang.school.postservice.client;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLanguageRequest {

    @Bean
    public RequestInterceptor noOpInterceptor() {
        return template -> {
        };
    }
}