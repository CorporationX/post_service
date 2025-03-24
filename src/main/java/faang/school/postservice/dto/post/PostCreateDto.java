package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema
public class PostCreateDto {
    @NotBlank(message = "Текст поста не должен быть пустым")
    @Size(max = 4096)
    @Schema(description = "Содержимое поста", example = "Круто!")
    private String content;
    @Schema(description = "Идентификатор проекта", example = "1")
    private Long projectId;
    @Schema(description = "Идентификатор автора", example = "1")
    private Long authorId;
    private List<Long> hashtagIds;

    @FutureOrPresent(message = "Дата запланированной публикации не может быть меньше текущей")
    private LocalDateTime scheduledAt;
}
