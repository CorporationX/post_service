package faang.school.postservice.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostLikedEvent implements NotificationEvent {
    private Long likeId;
    private Long likerId;
    private Long authorId;
    private Long postId;
}
