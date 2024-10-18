package faang.school.postservice.model.event;

import lombok.Builder;

@Builder
public record PostEvent(
        long authorId,
        long postId
) {
}
