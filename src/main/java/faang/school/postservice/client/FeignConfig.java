package faang.school.postservice.client;

import faang.school.postservice.config.api.ApiProperties;
import faang.school.postservice.config.context.UserContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FeignConfig {

    @Bean
    public FeignUserInterceptor feignUserInterceptor(UserContext userContext,
                                                     List<ApiProperties> apiProperties) {
        Map<String, Map<String, List<String>>> endpointRequiredHeaders = new HashMap<>();

        apiProperties.forEach(p -> {
            Map<String, List<String>> portToHeadersMap = endpointRequiredHeaders
                    .computeIfAbsent(p.getHost(), k -> new HashMap<>());

            portToHeadersMap.put(p.getPort(), p.getRequiredHeaders());
        });

        return new FeignUserInterceptor(userContext, endpointRequiredHeaders);
    }
}
