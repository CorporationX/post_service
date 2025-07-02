package faang.school.postservice.dto.notification;

import faang.school.postservice.dto.like.LikeDto;
import lombok.Builder;

@Builder
public record CommentLikedEvent(
        LikeDto user,
        LikeDto comment
) implements NotificationEvent {
}