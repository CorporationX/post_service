package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record CashCommentDto(
        Long authorId,
        String text,
        LocalDateTime createdAt,
        Long likesCount
) {
}
