package faang.school.postservice.dto.post;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostDto {
    private long id;
    @NotBlank(message = "Пост должен содержать текст.")
    private String content;
    @Min(value = 0, message = "АйДи автора должно быть положительным.")
    @NotNull(message = "АйДи автора не может быть пустым.")
    private Long authorId;
    @Min(value = 0, message = "АйДи проекта должно быть положительным.")
    @NotNull(message = "АйДи проекта не может быть пустым.")
    private Long projectId;
}
