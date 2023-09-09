package faang.school.postservice.publisher.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent implements Serializable {
    private Long authorId;
    private Long postId;
    private Long commentId;
    private String commentText;
}
