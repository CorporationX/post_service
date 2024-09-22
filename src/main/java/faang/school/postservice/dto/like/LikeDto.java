package faang.school.postservice.dto.like;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LikeDto {
    @PositiveOrZero
    private Long Id;
    @PositiveOrZero
    private Long userId;
    @PositiveOrZero
    private Long commentId;
    @PositiveOrZero
    private Long postId;
}
