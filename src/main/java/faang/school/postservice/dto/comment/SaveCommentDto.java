package faang.school.postservice.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Данные для создания или обновления комментария")
public record SaveCommentDto(
        @Schema(description = "Текст комментария, не может быть пустым, не должен превышать 4096 символов")
        @NotBlank(message = "Введите текст комментария")
        @Size(max = 4096, message = "Заголовок не должен превышать 4096 символов")
        String content
) {
}
