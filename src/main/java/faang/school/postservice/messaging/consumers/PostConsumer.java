package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.post.PostCacheDto;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
@RequiredArgsConstructor
public class PostConsumer {
    private final RedisFeedRepository repository;

    @KafkaListener(
            topics = "post-publication",
            groupId = "group"
    )
    public void listen(PostCacheDto postCacheDto) {
        repository.save(postCacheDto.getAuthorId(), postCacheDto.getPostId());
    }
}
