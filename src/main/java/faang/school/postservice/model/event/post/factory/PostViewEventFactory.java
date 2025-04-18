package faang.school.postservice.model.event.post.factory;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.Event;
import faang.school.postservice.util.EventTypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostViewEventFactory {
    private final List<EventCreator<?>> creators;
    private final Clock clock;

    private Map<Class<? extends Event>, EventCreator<?>> creatorMap;

    @PostConstruct
    public void init() {
        creatorMap = EventTypeUtils.createEventMap(creators, EventCreator::getEventType);
    }

    public List<Event> createEvents(Post post, Long viewerId, Class<? extends Event>[] eventTypes) {
        LocalDateTime viewedAt = LocalDateTime.now(clock);
        return Arrays.stream(eventTypes)
                .map(eventType -> createEvent(eventType, post, viewerId, viewedAt))
                .toList();
    }

    private Event createEvent(Class<? extends Event> eventType, Post post,
                              Long viewerId, LocalDateTime viewedAt) {
        EventCreator<?> creator = creatorMap.get(eventType);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown event type: " + eventType);
        }
        if (!eventType.isAssignableFrom(creator.getEventType())) {
            throw new IllegalStateException("Creator type mismatch for event: " + eventType);
        }
        return creator.createEvent(post, viewerId, viewedAt);
    }
}
