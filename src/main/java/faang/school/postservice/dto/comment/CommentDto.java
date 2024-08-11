package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto implements Serializable {
    private Long id;

    @NotBlank(message = "Содержание комментария должно быть предоставлено и не может быть пустым")
    @Size(max = 4096, message = "Содержание комментария не может содержать более 4096 символов")
    private String content;

    @NotNull(message = "Необходимо указать id автора")
    private Long authorId;

    private Long postId;
    private LocalDateTime createdAt;
}
