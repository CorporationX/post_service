package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentEventDto {
    private Long postId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime createdAt;
}