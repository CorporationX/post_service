package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
@Builder
public class CommentEventDto implements Serializable {
    private long commentAuthorId;
    private long postAuthorId;
    private long postId;
    private long commentId;
    private String commentText;
}
