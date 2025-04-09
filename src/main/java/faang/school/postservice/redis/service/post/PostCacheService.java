package faang.school.postservice.redis.service.post;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.dto.post.PostCreatedEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostCacheService {

    void savePostToCache(PostCacheDto post);

    void addPostView(PostCacheDto post);

    void updateFeedsInCache(PostCreatedEvent event);

    CompletableFuture<Void> saveAllPostsToCache(List<PostCacheDto> posts);
}
