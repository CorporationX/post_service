package faang.school.postservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectDto(
    @NotNull(message = "id should not be null")
    long id,
    @NotBlank(message = "title should not be blank")
    String title
) {
}
