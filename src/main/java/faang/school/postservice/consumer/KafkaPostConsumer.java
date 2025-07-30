package faang.school.postservice.consumer;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.utils.Feed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    private final Feed feed;

    @KafkaListener(topics = "${kafka.topics.posts.create-post}", groupId = "${kafka.group}")
    public void consume(PostEventDto event) {
        event.followeesIds().forEach(id -> feed.save(id, event.postId()));
    }
}
