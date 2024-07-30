package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
public interface ProjectServiceClient {

    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable @Positive long projectId);

    @PostMapping("/projects")
    List<ProjectDto> getProjectsByIds(@RequestBody @Positive List<Long> ids);
}
