package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
public class CommentEventDto {
    private Long commentId;
    private Long authorId;
    private Long postId;
    private Long receiverId;
    private LocalDateTime createdAt;
}