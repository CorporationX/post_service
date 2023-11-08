package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.model.PostPair;
import faang.school.postservice.model.RedisFeed;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;

@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class PostConsumer {
    @Value("${initial-capacity}")
    private int initialCapacity;
    private final RedisFeedRepository repository;

    @KafkaListener(
            topics = "post-cache-publication",
            groupId = "group"
    )
    public void listen(PostCacheDto postCacheDto) {
        log.info("PostConsumer has received: {}", postCacheDto);
        RedisFeed redisFeed = getRedisFeed(postCacheDto);
        repository.save(redisFeed);
        log.info("RedisFeedRepository has successfully saved: {}", redisFeed);
    }

    private RedisFeed getRedisFeed(PostCacheDto postCacheDto) {
        PostPair postPair = new PostPair();
        postPair.setAuthorId(postCacheDto.getAuthorId());
        postPair.setPostId(postCacheDto.getPostId());

        RedisFeed redisFeed = new RedisFeed();
        redisFeed.setUserId(postCacheDto.getAuthorId());
        redisFeed.setPosts(new LinkedHashSet<>(initialCapacity));
        redisFeed.getPosts().add(postPair);
        return redisFeed;
    }
}
