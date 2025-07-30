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
public class PostLikedEvent implements NotificationEvent {
    private Long postId;
    private String likerUsername;
    private UserDto owner;
    private String shortContent;
}
