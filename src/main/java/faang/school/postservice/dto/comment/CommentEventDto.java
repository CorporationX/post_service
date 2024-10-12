package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentEventDto {
    private Long postId;
    private Long receiverId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime createdAt;
}
