package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentEvent {
    private long id;
    private String content;
    private long authorId;
    private long postId;
}
