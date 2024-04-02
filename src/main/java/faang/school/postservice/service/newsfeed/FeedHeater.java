package faang.school.postservice.service.newsfeed;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
public class FeedHeater {

    private final PostRepository postRepository;
    private final PostService postService;
    private final RedisPostRepository redisPostRepository;
    private final Executor feedHeaterThreadPool;

    @Value("${batchSize.feed-batch}")
    private long postsBatchSize;

    public void generateFeeds() {
        if (postsBatchSize > postRepository.count())
            postsBatchSize = postRepository.count();
        for (long i = 1; i <= postsBatchSize; i++) {
            long postId = i;
            feedHeaterThreadPool.execute(() -> heatFeed(postId));
        }
    }

    private void heatFeed(long postId) {
        if (!redisPostRepository.existsById(postId)) {
            Optional<Post> optionalPost = postRepository.findById(postId);
            if (optionalPost.isPresent()) {
                Post post = optionalPost.get();
                postService.cachePost(post, (c1, c2) -> c1.getCreatedAt().compareTo(c2.getCreatedAt()));
                postService.cachePostAuthor(post.getAuthorId());
                postService.sendKafkaPostEvent(post);

            }
        }
    }
}
