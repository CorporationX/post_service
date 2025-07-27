package faang.school.postservice.event;

import faang.school.postservice.dto.notification.NotificationEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent implements NotificationEvent {
    private String content;
    private long postId;
    private long authorId;
}
