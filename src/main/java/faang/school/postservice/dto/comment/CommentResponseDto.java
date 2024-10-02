package faang.school.postservice.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
public class CommentResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private Collection<Long> likes;
    private Long postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
