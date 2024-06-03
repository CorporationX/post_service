package faang.school.postservice.dto.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectDto {
    @Min(value = 1, message = "ID cannot be less than or equal to 0")
    private long id;
    @NotBlank(message = "Title cannot be empty")
    private String title;
}
