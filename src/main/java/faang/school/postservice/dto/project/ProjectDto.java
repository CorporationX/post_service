package faang.school.postservice.dto.project;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectDto {
    @Min(0)
    private long id;
    @NotBlank(message = "Это поле не должно быть пустым и не должно содержать одни пробелы")
    private String title;
}
