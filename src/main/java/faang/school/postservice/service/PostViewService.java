package faang.school.postservice.service;

import faang.school.postservice.service.publisher.KafkaPostViewProducer;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import faang.school.postservice.event.PostViewEvent;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.repository.PostRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostViewService {
    private static final String INCREMENT_SCRIPT =
            "local current = redis.call('GET', KEYS[1])\n" +
                    "if current then\n" +
                    "    return redis.call('INCR', KEYS[1])\n" +
                    "else\n" +
                    "    return nil\n" +
                    "end";

    private final RedisTemplate<String, String> redisTemplate;
    private final PostRepository postRepository;

    public void processPostView(Long postId, Long userId) {
        PostViewEvent event = new PostViewEvent(postId, userId, LocalDateTime.now());
        kafkaPostViewProducer.sendPostViewEvent(event);
    }
  
    public void handlePostViewEvent(PostViewEvent event) {
        int incrementViews = postRepository.incrementViews(event.getPostId());
        if (incrementViews == 0) {
            log.error("Post is not found");
            throw new PostNotFoundException(String.format("Post is not found %s",
                    event.getPostId().toString()));
        }

        String postKey = "posts:" + event.getPostId() + ":views";

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(INCREMENT_SCRIPT);
        script.setResultType(Long.class);

        Long result = redisTemplate.execute(
                script,
                Collections.singletonList(postKey)
        );

        if (result == null) {
            log.error("Post is not found {} in Redis", event.getPostId().toString());
            throw new PostNotFoundException(String.format("Post is not found %s in Redis",
                    event.getPostId().toString()));
        }
    }
}
