package faang.school.postservice.dto.commentanalysis;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AnalysisCommentsEventDto(
        Long postId,
        Long authorId,
        Long commentId,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt
) {
}