package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventDto {
    private long commentAuthorId;
    private long postAuthorId;
    private long postId;
    private String content;
    private long commentId;
}
