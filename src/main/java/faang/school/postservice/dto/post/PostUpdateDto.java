package faang.school.postservice.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
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
public class PostUpdateDto {
    @Size(max = 4096)
    @Schema(
            description = "Содержимое поста",
            example = "Круто!",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED
    )
    private String content;
    private List<Long> hashtagIds;

    @FutureOrPresent(message = "Дата запланированной публикации не может быть меньше текущей")
    private LocalDateTime scheduledAt;
}
