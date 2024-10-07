package faang.school.postservice.client;

import faang.school.postservice.config.context.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FeignUserInterceptor implements RequestInterceptor {

    private static final String HEADER_X_USER_ID = "x-user-id";

    private final UserContext userContext;
    private final Map<String, Map<String, List<String>>> endpointRequiredHeaders;
    private final Map<String, Supplier<String>> headerMapping = new HashMap<>();

    public FeignUserInterceptor(UserContext userContext, Map<String, Map<String, List<String>>> endpointRequiredHeaders) {
        this.userContext = userContext;
        this.endpointRequiredHeaders = endpointRequiredHeaders;
        initializeHeaderMapping();
    }

    @Override
    public void apply(RequestTemplate template) {
        endpointRequiredHeaders.forEach((key, value) -> {
            if (template.feignTarget().url().contains(key)) {
                endpointRequiredHeaders.get(key).forEach((k, v) -> {
                    if (template.feignTarget().url().contains(k)) {
                        List<String> headers = endpointRequiredHeaders.get(key).get(k);
                        if (headers != null && !headers.isEmpty()) {
                            addHeaderToTemplate(template, headers);
                        }
                    }
                });
            }
        });
    }

    private void initializeHeaderMapping() {
        headerMapping.put(HEADER_X_USER_ID, () -> String.valueOf(userContext.getUserId()));
    }

    private void addHeaderToTemplate(RequestTemplate template, List<String> headers) {
        headers.forEach(header -> {
            Supplier<String> supplier = headerMapping.get(header);
            if (supplier != null) {
                template.header(header, supplier.get());
            }
        });
    }
}
