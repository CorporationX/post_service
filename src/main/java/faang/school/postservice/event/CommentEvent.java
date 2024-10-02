package faang.school.postservice.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
}
