package faang.school.postservice.dto;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PostFeedResponseDto(
        Long id,
        Long authorId,
        String content,
        Long projectId,
        Instant publishedAt,
        int likes,
        List<CommentDto> comments
) {
}
