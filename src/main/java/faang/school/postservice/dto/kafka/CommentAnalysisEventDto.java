package faang.school.postservice.dto.kafka;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentAnalysisEventDto(
    Long id,
    Long postId,
    Long authorId,
    Long commentId,
    LocalDateTime createdAt
) {
}