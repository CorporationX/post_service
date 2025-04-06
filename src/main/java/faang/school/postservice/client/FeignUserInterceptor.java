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
        userContext.getUserIdOptional().ifPresent(userId ->
                template.header("x-user-id", String.valueOf(userId)));
    }
}
