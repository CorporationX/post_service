package faang.school.postservice.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProjectDto {
    @NotNull(message = "ProjectId can not be null")
    @Positive(message = "ProjectId should be positive")
    private long id;

    @NotBlank(message = "Title can not be blank")
    private String title;
}
