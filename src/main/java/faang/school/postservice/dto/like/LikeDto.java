package faang.school.postservice.dto.like;

import java.time.LocalDateTime;

public record LikeDto(
        Long id,
        Long userId,
        Long postId,
        Long commentId,
        LocalDateTime createdAt
) {
    public LikeDto {
        boolean hasPost = postId != null;
        boolean hasComment = commentId != null;
        if (hasPost == hasComment) {
            throw new IllegalArgumentException("PostId or commentId must be provided.");
        }
    }
}
