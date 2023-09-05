package faang.school.postservice.event.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentEvent {
    private Long commentId;
    private Long authorCommentId;
    private Long authorPostId;
    private String content;
}
