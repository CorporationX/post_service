package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utils.Feed;
import faang.school.postservice.utils.PostCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final Feed feed;
    private final PostService service;
    private final PostCache postCache;

    @KafkaListener(topics = "${kafka.topics.posts}")
    public void consume(PostEventDto event) {
        event.followeesIds().forEach(id -> {
            feed.save(id, event.postId());
            postCache.save(event);
        });
    }
}
