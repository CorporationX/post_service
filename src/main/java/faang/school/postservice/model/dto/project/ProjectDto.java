package faang.school.postservice.model.dto.project;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectDto {
    @Positive
    private long id;

    @NotBlank(message = "Title can not be null or empty")
    @Max(128)
    private String title;
}
