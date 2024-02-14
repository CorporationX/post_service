package faang.school.postservice.dto.project;

import faang.school.postservice.model.ProjectStatus;
import faang.school.postservice.model.ProjectVisibility;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectDto {
    private Long id;
    private String name;
    private String description;
    @Min(1)
    private Long parentId;
    @Min(1)
    private long ownerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProjectStatus status;
    private ProjectVisibility visibility;
}