package faang.school.postservice.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

@Builder
@AllArgsConstructor
public class CommentLikedEvent implements NotificationEvent {
    private long likeId;
    private long commentId;
}