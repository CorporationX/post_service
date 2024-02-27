package faang.school.postservice.client;

import faang.school.postservice.dto.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {

    @GetMapping("/api/v1/project/{projectId}")
    ProjectDto getProject(@PathVariable("projectId") long projectId);

    @PostMapping("/api/v1/projects")
    List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/projects/exists/{projectId}")
    void existsProjectById(@PathVariable("projectId") long projectId);
}
