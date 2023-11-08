package faang.school.postservice.dto.project;

import faang.school.postservice.model.ProjectStatus;
import faang.school.postservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {

    private long id;

    @NotBlank
    private String title;

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    private Long ownerId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    private ProjectStatus status;

    private ProjectVisibility visibility;
}
