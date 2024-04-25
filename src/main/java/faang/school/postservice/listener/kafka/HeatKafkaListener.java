package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.publisher.kafka.PostKafkaPublisher;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

@RequiredArgsConstructor
public class HeatKafkaListener extends AbstractKafkaListener<Long> {
    private final PostService postService;
    private final PostKafkaPublisher postKafkaPublisher;

    @KafkaListener(topics = "${kafka.topics.heat.name}", groupId = "${kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, Object> message) {
        Long userId = getEvent(message, Long.class);

        postService.getPosts(userId, 100).forEach(
                post -> postKafkaPublisher.publish(post.getId(), post.getAuthorId(), post.getPublishedAt(), KafkaKey.SAVE)
        );
    }
}
