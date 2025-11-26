package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private long authorId;
    private long postId;
    private long commentId;
    private String content;
}