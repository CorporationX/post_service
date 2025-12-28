package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedPostDto;

import java.util.List;

public interface FeedService {

    List<FeedPostDto> getFeedList(Long lastPostId);
}
