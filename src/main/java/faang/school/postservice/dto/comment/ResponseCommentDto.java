package faang.school.postservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class ResponseCommentDto {
    @NotNull(message = "Id комментария не должен быть null")
    @Positive(message = "Id комментария должен быть положительным")
    private Long id;

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 4036, message = "Комментарий должен быть не меньше 1 символа и не больше 4036")
    private String content;

    @NotNull(message = "Id автора комментария не должен быть null")
    @Positive(message = "Id автора комментария должен быть положительным")
    private Long authorId;

    @NotNull(message = "Список комментария не должен быть null")
    private List<Long> likeIds;

    @NotNull(message = "Id поста комментария не должен быть null")
    @Positive(message = "Id поста комментария должен быть положительным")
    private Long postId;

    @NotNull(message = "Дата создания не может быть пустой")
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
