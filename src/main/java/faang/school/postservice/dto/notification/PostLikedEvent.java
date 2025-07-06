package faang.school.postservice.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class PostLikedEvent implements NotificationEvent {
    private long likeId;
    private long postId;
}
