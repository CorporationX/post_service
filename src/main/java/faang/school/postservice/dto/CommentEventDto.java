package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEventDto implements Serializable {
    private long postId;
    private long authorId;
    private long authorPostId;
    private long commentId;
    private String content;
}
