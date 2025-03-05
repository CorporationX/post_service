package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class CommentDto {
    long id;

    @NotBlank
    @Size(min = 1, max = 4036, message = "Комментарий должен быть от 1 до 4036 символов")
    String content;

    @Positive(message = "id автора должен быть больше ноля")
    long authorId;

    @Positive(message = "Id поста должен быть больше ноля")
    long postId;

    @NotNull(message = "Список комментария не должен быть null")
    private List<Long> likeIds;

    @NotNull(message = "Дата создания не может быть пустой")
    LocalDateTime createdAt;
}