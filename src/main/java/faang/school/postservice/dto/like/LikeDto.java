package faang.school.postservice.dto.like;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record LikeDto(
        @Positive(message = "Like Id can't be negative")
        long id,
        long postId,
        long commentId,
        @Positive(message = "User Id can't be negative")
        long userId
) {
}
