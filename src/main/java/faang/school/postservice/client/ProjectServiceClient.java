package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectClientResponseDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "project-service",
        url = "${project-service.host}:${project-service.port}",
        path = "/api/v1/project",
        configuration = FeignConfig.class)
public interface ProjectServiceClient {
    @GetMapping("/{projectId}")
    @Valid ProjectClientResponseDto getProject(@PathVariable long projectId);

    @GetMapping()
    @Valid List<ProjectClientResponseDto> getProjectsByIds(@RequestParam List<Long> ids);
}
