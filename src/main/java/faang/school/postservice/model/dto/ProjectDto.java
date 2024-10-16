package faang.school.postservice.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectDto {
    private long id;
    @NotBlank
    @Size(min = 2, max = 128)
    private String title;
}
