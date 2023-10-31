package faang.school.postservice.messaging.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent implements Serializable {
    private long id;
    private String content;
    private long authorId;
    private long postId;
}
