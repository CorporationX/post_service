package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.FeedRequest;
import faang.school.postservice.dto.post.PostDto;

import java.util.List;

public interface NewsFeedService {
    List<PostDto> getUserFeed(FeedRequest request);
}
