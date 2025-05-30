package faang.school.postservice.mapper;

import faang.school.postservice.model.outbox.OutboxFeedEvent;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventMapper {

    public String mapToTopic(OutboxFeedEvent event) {
        return switch (event.getAggregateType()) {
            case POST -> "posts";
            case LIKE -> "likes";
            case COMMENT -> "comments";
            case POST_VIEW -> "post_views";
        };
    }
}
