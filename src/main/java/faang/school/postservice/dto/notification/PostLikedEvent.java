package faang.school.postservice.dto.notification;

import faang.school.postservice.dto.like.LikeDto;
import lombok.Builder;

@Builder
public record PostLikedEvent(
        LikeDto user,
        LikeDto post
) implements NotificationEvent {
}
