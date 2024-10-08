package faang.school.postservice.kafka.events;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;

import java.util.List;

@Builder
public record PostLikeEvent(
        Long id,
        String content,
        Long authorId,
        Integer likes,
        List<CommentDto> comments
) {}