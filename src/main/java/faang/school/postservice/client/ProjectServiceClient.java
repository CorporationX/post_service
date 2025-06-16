package faang.school.postservice.client;

import faang.school.postservice.config.client.feign.FeignClientConfig;
import faang.school.postservice.dto.project.ProjectClientResponseDto;
import feign.FeignException;
import feign.RetryableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "project-service",
        url = "${services.project-service.host}:${services.project-service.port}",
        path = "/api/v1/project",
        configuration = FeignClientConfig.class)
public interface ProjectServiceClient {
    @Retryable(
            retryFor = { FeignException.class, RetryableException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @GetMapping("/{projectId}")
    ProjectClientResponseDto getProject(@PathVariable long projectId);
    @Retryable(
            retryFor = { FeignException.class, RetryableException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    @GetMapping()
    List<ProjectClientResponseDto> getProjectsByIds(@RequestParam List<Long> ids);
}
