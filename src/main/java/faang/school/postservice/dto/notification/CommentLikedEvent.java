package faang.school.postservice.dto.notification;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikedEvent implements NotificationEvent {
    private Long likeId;
    private Long likerId;
    private UserDto owner;
    private Long commentId;
}