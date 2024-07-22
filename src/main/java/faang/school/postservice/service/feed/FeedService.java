package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedPublicationDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {
    List<FeedPublicationDto> getNewsFeed(long userId, Pageable pageable);
}
