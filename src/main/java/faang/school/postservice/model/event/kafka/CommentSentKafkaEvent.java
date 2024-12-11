package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommentSentKafkaEvent {
    private Long postId;
    private Long commentAuthorId;
    private Long commentId;
    private String commentContent;
}
