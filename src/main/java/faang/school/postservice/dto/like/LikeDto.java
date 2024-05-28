package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Long id;
    @NotNull(message = "UserId не должен быть null")
    private Long userId;
    @Positive(message = "CommentId должен быть положительным")
    private Long commentId;
    @Positive(message = "PostId должен быть положительным")
    private Long postId;
}
