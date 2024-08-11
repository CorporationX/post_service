package faang.school.postservice.client;

import faang.school.postservice.dto.project.ProjectDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "project-service", url = "${project-service.host}:${project-service.port}")
@Validated
public interface ProjectServiceClient {

    @GetMapping("/project/{projectId}")
    ProjectDto getProject(@PathVariable @NotNull(message = "Поле не может отсутствовать!")
                          @Min(value = 1,
                                  message = "Поле должно быть 1 или более.") long projectId);

    @PostMapping("/projects")
    List<ProjectDto> getProjectsByIds(@RequestBody
                                      @NotEmpty(message = "Список не должен быть пустым.")
                                      List<Long> ids);
}
