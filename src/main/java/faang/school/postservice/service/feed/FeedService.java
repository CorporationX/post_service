package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.FeedPostDto;

import java.util.List;

public interface FeedService {
    List<FeedPostDto> getFeedPage(long userId, Long afterPostId);
}
