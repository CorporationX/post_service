package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
@Retryable(
        retryFor = {SocketTimeoutException.class,
                ConnectException.class,
                FeignException.ServiceUnavailable.class,
                FeignException.GatewayTimeout.class},
        backoff = @Backoff(delay = 1000, multiplier = 2)
)
public interface ProjectServiceClient {
    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable long projectId);

    @PostMapping("/projects")
    List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids);

    @GetMapping("/project/{projectId}/exists")
    ResponseEntity<Void> checkProjectExists(@PathVariable Long projectId);
}
