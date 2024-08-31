package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent extends Event {

    private Long postId;

    private Long authorId;

    private Long commentId;

    private LocalDateTime sendAt;
}
