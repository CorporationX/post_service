package faang.school.postservice.event;

import faang.school.postservice.dto.notification.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent implements NotificationEvent {
    private long id;
    private String content;
    private long postId;
    private long authorId;
    private LocalDateTime cratedAt;
}
