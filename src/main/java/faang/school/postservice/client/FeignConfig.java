package faang.school.postservice.client;

import faang.school.postservice.client.handler.FeignErrorHandler;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.ErrorResponseMapper;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FeignConfig {
    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext) {
        return new FeignUserInterceptor(userContext);
    }

    @Bean
    public ErrorDecoder errorDecoder(List<FeignErrorHandler> handlers,
                                     ErrorResponseMapper errorResponseMapper) {
        return new FeignClientErrorDecoder(handlers, errorResponseMapper);
    }
}
