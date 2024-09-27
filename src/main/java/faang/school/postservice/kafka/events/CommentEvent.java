package faang.school.postservice.kafka.events;

import lombok.Builder;

@Builder
public record CommentEvent(
        Long commentId,
        Long authorId,
        Long postId,
        String content
) {}