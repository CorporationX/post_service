package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventDto {
    private Long commentAuthorId;
    private Long postId;
    private Long commentId;
    private String commentText;
}
