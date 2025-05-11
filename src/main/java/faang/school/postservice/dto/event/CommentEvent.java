package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private Long authorId;
    private Long postId;
    private Long commentId;
    private String text;
}
