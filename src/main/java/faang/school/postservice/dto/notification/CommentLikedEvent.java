package faang.school.postservice.dto.notification;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Post;
import lombok.Builder;

@Builder
public record CommentLikedEvent(
        Post user,
        LikeDto like
) implements NotificationEvent {
}