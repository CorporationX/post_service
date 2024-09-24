package faang.school.postservice.dto.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectDto {
    @Min(value = 1, message = "ID must be a positive number")
    private long id;
    @NotBlank(message = "Title should not be blank")
    private String title;
}
