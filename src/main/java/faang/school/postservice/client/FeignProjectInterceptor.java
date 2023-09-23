package faang.school.postservice.client;

import faang.school.postservice.config.context.ProjectContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignProjectInterceptor implements RequestInterceptor {
    private final ProjectContext projectContext;

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-project-id", String.valueOf(projectContext.getProjectId()));
    }
}
