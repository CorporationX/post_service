package faang.school.postservice.event.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CommentKafkaEvent {
    private long id;
    private String content;
    private long authorId;
    private long postId;
    private LocalDateTime createdAt;

}
