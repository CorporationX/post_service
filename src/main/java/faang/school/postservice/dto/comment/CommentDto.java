package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record CommentDto(
    Long id,
    Long authorId,
    Long postId,
    String content,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
){
}
