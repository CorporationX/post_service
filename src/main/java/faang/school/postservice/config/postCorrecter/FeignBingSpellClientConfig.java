package faang.school.postservice.config.postCorrecter;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignBingSpellClientConfig implements RequestInterceptor {
    private final BingSpellConfig bingSpellConfig;

    @Override
    public void apply(RequestTemplate template) {
        template.headers(bingSpellConfig.getHeaders());
    }
}
