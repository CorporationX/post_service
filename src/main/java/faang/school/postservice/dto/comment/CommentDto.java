package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;

@Jacksonized
@Builder
public record CommentDto(
        long id,
        long authorId,
        long postId,
        String content,
        LocalDateTime createdAt
) {
}
