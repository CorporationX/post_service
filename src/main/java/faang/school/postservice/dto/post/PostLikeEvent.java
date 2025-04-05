package faang.school.postservice.dto.post;

import lombok.Builder;

@Builder
public record PostLikeEvent(
        long postId
) {
}
