package faang.school.postservice.event.comment;

import faang.school.postservice.dto.notification.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent implements NotificationEvent {

    private long id;
    private long postId;
    private long authorId;
    private long authorPostId;
    private LocalDateTime cratedAt;
}
