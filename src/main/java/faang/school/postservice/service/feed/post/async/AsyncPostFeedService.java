package faang.school.postservice.service.feed.post.async;

import faang.school.postservice.dto.feed.PostFeedDto;

import java.util.concurrent.CompletableFuture;

public interface AsyncPostFeedService {

    CompletableFuture<PostFeedDto> getPostsWithAuthor(long postId, long userId);
}
