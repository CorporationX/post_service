package faang.school.postservice.service.feed.post;

import faang.school.postservice.dto.feed.PostFeedDto;

public interface PostFeedService {

    PostFeedDto getPostsWithAuthor(long postId);
}
