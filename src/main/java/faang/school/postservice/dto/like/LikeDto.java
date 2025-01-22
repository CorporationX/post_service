package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LikeDto {
    @Positive(message = "Comment ID must be positive")
    Long commentId;
    @Positive(message = "Post ID must be positive")
    Long postId;
}