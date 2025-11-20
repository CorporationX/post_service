package faang.school.postservice.dto.project;

import jakarta.validation.constraints.NotBlank;

public record ProjectDto(
        long id,
        @NotBlank(message = "Project title cannot be empty")
        String title
) {
}
