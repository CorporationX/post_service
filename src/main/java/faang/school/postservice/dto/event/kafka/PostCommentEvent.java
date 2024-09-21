package faang.school.postservice.dto.event.kafka;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentEvent {
    private long postId;
    private long authorId;
}
