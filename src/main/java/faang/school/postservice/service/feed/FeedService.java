package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.PostFeedDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {
    List<PostFeedDto> getNewsFeed(long userId, Pageable pageable);
}
