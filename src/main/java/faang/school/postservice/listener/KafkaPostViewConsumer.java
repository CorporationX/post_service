package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostViewAddEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.PostView;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.PostViewService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Service
@RequiredArgsConstructor
public class KafkaPostViewConsumer {

    private final RedisPostRepository redisPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostViewService postViewService;

    @KafkaListener(topics = "${spring.kafka.topics.post-views}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostViewAddEvent postViewAddEvent) {
        Long postId = postViewAddEvent.postId();
        Long postViewId = postViewAddEvent.id();

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            @SuppressWarnings("unchecked")
            public <K, V> List<Object> execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                operations.watch((K) ("post:post-views:" + postId));
                RedisPost post = getPost(postId);
                PostView postView = postViewService.getPostView(postViewId);
                if (!post.getPostViews().contains(postView)) {
                    operations.multi();
                    post.getPostViews().add(postView);
                    redisPostRepository.save(post);
                    return operations.exec();
                }
                operations.unwatch();
                return null;
            }
        });
    }

    private RedisPost getPost(long postId) {
        return redisPostRepository.findById(postId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Post not found by Id: %d", postId)));
    }
}
