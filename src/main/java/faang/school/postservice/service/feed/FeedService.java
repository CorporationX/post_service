package faang.school.postservice.service.feed;

import faang.school.postservice.dto.kafka.PostEvent;

public interface FeedService {
    void updateFeeds(PostEvent postEvent) throws InterruptedException;
}
