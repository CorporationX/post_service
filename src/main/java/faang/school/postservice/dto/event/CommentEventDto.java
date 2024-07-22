package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentEventDto extends EventDto implements Serializable {
    private long commentId;
    private long commentAuthorId;
    private long postAuthorId;
    private long postId;
    private String commentText;
}
