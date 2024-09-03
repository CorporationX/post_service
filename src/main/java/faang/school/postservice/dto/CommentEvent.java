package faang.school.postservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentEvent {
    private Long postId;
    private Long receiverId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime createdAt;
}
