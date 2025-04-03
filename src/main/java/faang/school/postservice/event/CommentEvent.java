package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommentEvent {
    private Long commentId;
    private String comment;
    private Long userId;
    private Long postId;
    private Integer likesCount;
    private LocalDateTime createdAt;
}
