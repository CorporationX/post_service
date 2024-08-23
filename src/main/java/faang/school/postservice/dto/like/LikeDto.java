package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LikeDto {
    private Long likeId;
    @NotNull
    private Long userId;
    private Long commentId;
    private Long postId;
}
