package faang.school.postservice.kafka.event.comment;

import faang.school.postservice.kafka.event.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(callSuper = false)
public class CommentAddedEvent extends Event {
    private Long commentId;
    private String content;
    private Long authorId;
    private Long postId;
}
