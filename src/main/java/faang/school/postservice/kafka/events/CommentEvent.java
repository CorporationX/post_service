package faang.school.postservice.kafka.events;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;

@Builder
public record CommentEvent(
        CommentDto commentDto,
        Long authorId,
        Long postId,
        String content
) {}