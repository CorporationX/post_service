package faang.school.postservice.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentAnalytics implements NotificationEvent {
    Long postId;
    Long authorId;
    Long commentId;
    String createdAt;
}