package faang.school.postservice.service.feed;

import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface FeedAsyncUpdater {

    @Async("feedTaskExecutor")
    void addPostChunkToFeeds(Long postId, Long timestamp, List<Long> chunk);
}
