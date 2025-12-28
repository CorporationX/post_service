package faang.school.postservice.dto.event;

import lombok.Builder;

@Builder
public record CommentEventDto(
        long authorId,
        long postId,
        long commentId,
        String content) {
}