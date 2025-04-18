package faang.school.postservice.model.event.post.factory;

import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.post.view.AnalyticsPostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AnalyticsEventCreator implements EventCreator<AnalyticsPostViewEvent> {
    private final PostViewEventMapper postViewEventMapper;
    @Override
    public Class<AnalyticsPostViewEvent> getEventType() {
        return AnalyticsPostViewEvent.class;
    }

    @Override
    public AnalyticsPostViewEvent createEvent(Post post, Long viewerId, LocalDateTime viewedAt) {
        return postViewEventMapper.toAnalyticsEvent(post, viewerId, viewedAt);
    }
}
