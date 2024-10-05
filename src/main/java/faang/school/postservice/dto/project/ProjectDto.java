package faang.school.postservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectDto {
    private long id;
    @NotBlank(message = "Title should not be blank")
    @Size(min = 2, max = 128)
    private String title;
}
