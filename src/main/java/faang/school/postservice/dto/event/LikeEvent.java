package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record LikeEvent(long postAuthorId,
                        long likeAuthorId,
                        long postId) {
}
