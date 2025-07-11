package faang.school.postservice.dto.notification;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreationNotificationEvent implements NotificationEvent {
    private String shortContent;
    private UserDto postAuthor;
    private String commentAuthorUserName;
}
