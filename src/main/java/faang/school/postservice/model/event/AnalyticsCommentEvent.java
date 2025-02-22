package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnalyticsCommentEvent implements Event {
    private long postId;
    private long authorId;
    private long commentId;
    private LocalDateTime timestamp = LocalDateTime.now();
}