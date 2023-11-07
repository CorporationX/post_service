package faang.school.postservice.messaging.consumers;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.model.PostPair;
import faang.school.postservice.model.RedisFeed;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;


@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class PostConsumer {
    private final RedisFeedRepository repository;

    @KafkaListener(
            topics = "post-cache-publication",
            groupId = "group"
    )
    public void listen(PostCacheDto postCacheDto) {
        log.info("PostConsumer has received: {}", postCacheDto);

        PostPair postPair = new PostPair();
        postPair.setAuthorId(postCacheDto.getAuthorId());
        postPair.setPostId(postCacheDto.getPostId());

        RedisFeed redisFeed = new RedisFeed();
        redisFeed.setUserId(postCacheDto.getAuthorId());
        redisFeed.setPosts(new LinkedHashSet<>(500));
        redisFeed.getPosts().add(postPair);

        repository.save(redisFeed);
        log.info("RedisFeedRepository has successfully saved: {}", redisFeed);
    }
}
