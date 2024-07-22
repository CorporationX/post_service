package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {
    List<PostDto> getNewsFeed(long userId, Pageable pageable);
}
