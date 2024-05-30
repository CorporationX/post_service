package faang.school.postservice.controller;

import faang.school.postservice.dto.project.ProjectDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @PostMapping("/post/{id}")
    @Operation(description = "Method Example")
    public ProjectDto getId(@PathVariable("id") Long id, ProjectDto projectDto) {

        return ProjectDto.builder().id(id).title(projectDto.getTitle()).build();
    }
}