package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record LikeEvent(
        long likeId,
        Long postId,
        Long commentId,
        long authorId
) {}
