package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}", path = "/api/v1/projects")
public interface ProjectServiceClient {

    @GetMapping
    List<ProjectDto> getAll();

    @GetMapping("{projectId}/exist")
    boolean existProjectById(@PathVariable("projectId") long projectId);
}