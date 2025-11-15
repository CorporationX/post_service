package faang.school.postservice.client;

import faang.school.postservice.config.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {

    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        Long userId = userContext.getUserId();

        if (userId != null) {
            template.header("x-user-id", String.valueOf(userId));
        }
    }
}