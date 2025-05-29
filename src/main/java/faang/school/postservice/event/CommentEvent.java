package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEvent {
    private Long authorId;
    private Long postId;
    private Long commentId;
    private String text;
    private LocalDateTime createdAt;
}
