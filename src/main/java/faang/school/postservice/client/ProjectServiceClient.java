package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {
    @GetMapping("/project/{projectId}")
    ProjectClientResponseDto getProject(@PathVariable long projectId);

    @PostMapping("/projects")
    List<ProjectClientResponseDto> getProjectsByIds(@RequestBody List<Long> ids);
}
