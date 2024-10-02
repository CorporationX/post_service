package faang.school.postservice.service.like;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.kafka.KafkaLikeEvent;
import faang.school.postservice.model.redis.PostInRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostInRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final PostInRedisRepository redisPostRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @KafkaListener(topics = "likes", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveLikeEvent(KafkaLikeEvent event) {
        long postId = event.getPostId();
        Optional<PostInRedis> postInRedis = redisPostRepository.findById(postId);

        if (postInRedis.isPresent()) {
            PostInRedis post = postInRedis.get();
            post.getNumberOfLikes().getAndIncrement();
            redisPostRepository.save(post);
            log.info("Пост с обновлённым количеством лайков сохранён в кеше.");
        } else {
            Post post = postRepository.findById(postId).orElseThrow(
                    () -> new RuntimeException("Такого поста не существует!"));
            redisPostRepository.save(postMapper.entityToPostInRedis(post));
        }
    }
}
