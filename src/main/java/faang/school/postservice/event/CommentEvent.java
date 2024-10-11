package faang.school.postservice.event;

import lombok.Builder;

@Builder
public record CommentEvent(
        long commentAuthorId,
        long postAuthorId,
        long postId,
        String content,
        long commentId
) {
}
