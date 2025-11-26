package faang.school.postservice.dto.kafka;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentAnalysisEventDto(
        Long postId,
        Long authorId,
        Long commentId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Moscow")
        LocalDateTime createdAt
) {
}