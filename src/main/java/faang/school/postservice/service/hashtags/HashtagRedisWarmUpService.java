package faang.school.postservice.service.hashtags;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashtagRedisWarmUpService {

    @Value("${app.hashtags.max-cached-posts-per-hashtag}")
    private int maxCachedPosts;

    @Value("${app.thread-pool-size}")
    private int threadPoolSize;

    private final RedisTemplate<String, String> redisTemplate;
    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final HashtagRedisService hashtagRedisService;
    //private final ExecutorService executor = Executors.newFixedThreadPool(1);

    public void warmUpCache() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });
        log.info("Hashtags cache warm-up started");
        List<Hashtag> hashtagList = hashtagRepository.findAll();
        Pageable pageable = PageRequest.of(0, maxCachedPosts);

        for (Hashtag hashtag : hashtagList) {
            // executor.submit(() -> {
                List<Post> posts = postRepository.findPostsByHashtag(pageable, hashtag.getTag()).getContent();
                posts.forEach(post -> hashtagRedisService.saveHashtag(hashtag.getTag(), post));
            //});
        }
        //executor.shutdown();
        log.info("Hashtags cache warm-up completed successfully");
    }
}
