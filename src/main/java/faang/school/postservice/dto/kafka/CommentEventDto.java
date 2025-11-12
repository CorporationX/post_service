package faang.school.postservice.dto.kafka;

import lombok.Builder;

@Builder
public record CommentEventDto(
        Long commentId,
        Long postId,
        Long commentAuthorId,
        Long postAuthorId,
        String commentText,
        String commentAuthorName,
        String postContent
) {}
