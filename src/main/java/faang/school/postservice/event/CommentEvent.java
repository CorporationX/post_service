package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent implements Serializable {
    private Long postId;
    private Long commentId;
    private Long commentAuthorId;
    private Long postAuthorId;
    private String commentText;
}
