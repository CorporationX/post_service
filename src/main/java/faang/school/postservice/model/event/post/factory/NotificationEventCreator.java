package faang.school.postservice.model.event.post.factory;

import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.view.NotificationPostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationEventCreator implements EventCreator<NotificationPostViewEvent> {
    private final PostViewEventMapper postViewEventMapper;
    @Override
    public Class<NotificationPostViewEvent> getEventType() {
        return NotificationPostViewEvent.class;
    }

    @Override
    public NotificationPostViewEvent createEvent(Post post, Long viewerId, LocalDateTime viewedAt) {
        return postViewEventMapper.toNotificationEvent(post, viewerId, viewedAt);
    }
}
