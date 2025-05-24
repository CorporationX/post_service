package faang.school.postservice.kafkalistener;

import faang.school.postservice.dto.kafkaevents.PostEvent;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.Instant;

@RequiredArgsConstructor
@Service
@Slf4j
public class PostEventListener {
    private final PostCacheService postCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;

    @Value("${spring.data.redis.feed.size}")
    private long feedSize;
    private static final String POSTS_HASH_KEY = "posts";

    @KafkaListener(topics = "${spring.data.kafka.topic.posts}")
    public void handlePostEvent(PostEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("Получил ивент для поста {}", event.postId());

            CachedPost post = getOrLoadPost(event.postId());

            event.followers().parallelStream().forEach(followerId -> {
                postCacheService.addToUserFeed(post, followerId);
            });

            acknowledgment.acknowledge();
            log.info("Успешно отправил ивент поста {} для {} подписчиков",
                    event.postId(), event.followers().size());

        } catch (EntityNotFoundException e) {
            log.error("Пост {} не найден в базе", event.postId());
        } catch (Exception e) {
            log.error("Ошибка отправка ивента поста {}", event.postId(), e);
        }
    }

    private CachedPost getOrLoadPost(Long postId) {
        CachedPost post = (CachedPost) redisTemplate.opsForHash()
                .get(POSTS_HASH_KEY, postId.toString());


        if (post == null) {
            Post dbPost = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

            post = CachedPost.builder()
                    .id(dbPost.getId())
                    .content(dbPost.getContent())
                    .publishedAt(Instant.from(dbPost.getPublishedAt()))
                    .projectId(dbPost.getProjectId())
                    .authorId(dbPost.getAuthorId())
                    .build();

            postCacheService.cachePost(post);
        }

        return post;
    }

}

