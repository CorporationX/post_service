package faang.school.postservice.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateDto {
    @NotNull
    @Positive
    private Long postId;

    @NotEmpty
    @Size(min = 1, max = 4096, message = "Комментарий может содержать от 1 до 4096 символов")
    private String content;

    @NotNull
    @Positive
    private Long authorId;
}
