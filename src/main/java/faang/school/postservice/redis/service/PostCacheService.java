package faang.school.postservice.redis.service;

import faang.school.postservice.model.dto.LikeDto;
import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.model.event.kafka.PostEventKafka;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostCacheService {

    void savePostToCache(PostDto post);

    void updatePostComments(CommentEventKafka event);

    void addPostView(PostDto post);

    void addPostLike(LikeDto like);

    void updateFeedsInCache(PostEventKafka event);

    CompletableFuture<Void> saveAllPostsToCache(List<PostDto> posts);
}
