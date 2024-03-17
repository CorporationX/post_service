package faang.school.postservice.client;

import faang.school.postservice.dto.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}", path = "/api/v1/projects")
public interface ProjectServiceClient {

    @GetMapping("/{projectId}")
    ProjectDto getProject(@PathVariable("projectId") long projectId);

    @PostMapping
    List<ProjectDto> getProjectsByIds(@RequestBody List<Long> ids);

    @GetMapping
    List<ProjectDto> getAll();

    @GetMapping("{projectId}/exist")
    boolean existProjectById(@PathVariable("projectId") long projectId);
}
