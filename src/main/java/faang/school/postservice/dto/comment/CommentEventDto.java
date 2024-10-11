package faang.school.postservice.dto.comment;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEventDto {
    private long userId;
    private long authorId;
    private long postId;
    private String content;
    private long commentId;
}
