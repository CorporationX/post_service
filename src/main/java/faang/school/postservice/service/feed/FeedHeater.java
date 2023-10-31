package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.KafkaFeedHeatEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.kafka.producers.KafkaFeedHeatProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.Executor;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedHeater {

    private final PostRepository postRepository;
    private final PostService postService;
    private final KafkaFeedHeatProducer kafkaFeedHeatProducer;
    private final RedisPostRepository redisPostRepository;
    private final Executor feedHeaterThreadPool;
    private final ZSetOperations<Long, Object> feeds;

    public void start() {
        sendKafkaEvent();
    }

    public void heat() {
        for (long i = 1; i <= postRepository.count(); i++) {
            long postId = i;
            feedHeaterThreadPool.execute(() -> heatFeed(postId));
        }
    }

    public void heatFeed(long postId) {
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

    public void sendKafkaEvent() {
        kafkaFeedHeatProducer.sendMessage(new KafkaFeedHeatEvent());
    }
}
