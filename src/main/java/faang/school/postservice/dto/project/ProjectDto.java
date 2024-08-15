package faang.school.postservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectDto {
    @NotNull(message = "id must not be null")
    private long id;

    @NotBlank(message = "title must not be blank")
    private String title;
}
