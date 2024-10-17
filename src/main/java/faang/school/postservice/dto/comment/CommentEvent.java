package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private Long postId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime timestamp;
}