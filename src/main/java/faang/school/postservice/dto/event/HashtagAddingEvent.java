package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record HashtagAddingEvent(
        String hashtagName,
        Long postId,
        Long authorId
) {
}
