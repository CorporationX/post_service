package faang.school.postservice.config.client.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {
    private final FeignClientConfigurationProperties props;
    @Override
    public void apply(RequestTemplate template) {
        template.header("x-user-id", String.valueOf(props.getUserId()));
    }
}
