package faang.school.postservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectDto {
    @NotNull(message = "Author ID cannot be null")
    private long id;
    @NotBlank(message = "Author ID cannot be blank")
    private String title;
}
