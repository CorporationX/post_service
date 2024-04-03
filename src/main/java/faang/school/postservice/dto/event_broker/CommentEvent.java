package faang.school.postservice.dto.event_broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private Long postId;
    private String content;
    private Long authorId;
}
