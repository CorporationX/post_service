package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedPostDto;

import java.util.List;

public interface FeedReadService {
    List<FeedPostDto> getFeed(Long userId, Long afterPostId, int limit);
}
