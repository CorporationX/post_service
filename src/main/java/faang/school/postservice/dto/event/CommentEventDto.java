package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentEventDto {
    private long commentAuthorId;
    private long postAuthorId;
    private long postId;
    private long commentId;
    private String commentText;
}
