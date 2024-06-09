package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Содержание комментария должно быть предоставлено и не может быть пустым")
    @Size(max = 4096, message = "Содержание комментария не может содержать более 4096 символов")
    private String content;

    @NotNull(message = "Необходимо указать id автора")
    private Long authorId;

    @NotNull
    private Long postId;
    private LocalDateTime createdAt;
}
