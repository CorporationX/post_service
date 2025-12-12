package faang.school.postservice.dto.commentanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalysisCommentsEventDto(
        Long receiverId,
        Long authorId,
        Long postId,
        Long commentId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
}