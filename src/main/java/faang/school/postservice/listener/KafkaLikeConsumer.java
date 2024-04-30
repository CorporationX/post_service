package faang.school.postservice.listener;

import faang.school.postservice.dto.event.LikeAddEvent;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.LikeService;
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
public class KafkaLikeConsumer {

    private final RedisPostRepository redisPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final LikeService likeService;

    @KafkaListener(topics = "${spring.kafka.topics.comments}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(LikeAddEvent likeAddEvent) {
        Long postId = likeAddEvent.postId();
        Long likeId = likeAddEvent.likeId();

        redisTemplate.execute(new SessionCallback<List<Object>>() {
            @Override
            @SuppressWarnings("unchecked")
            public <K, V> List<Object> execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                operations.watch((K) ("post:likes:" + postId));
                RedisPost post = getPost(postId);
                Like like = likeService.getLike(likeId);
                if (!post.getLikes().contains(like)) {
                    operations.multi();
                    post.getLikes().add(like);
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
