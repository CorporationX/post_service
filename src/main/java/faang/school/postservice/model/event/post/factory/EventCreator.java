package faang.school.postservice.model.event.post.factory;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.Event;

import java.time.LocalDateTime;

public interface EventCreator<T extends Event> {
    Class<T> getEventType();
    T createEvent(Post post, Long viewerId, LocalDateTime viewedAt);
}
