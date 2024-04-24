package faang.school.postservice.listener.kafka;

import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.PostViewedEventDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;

@KafkaListener
@RequiredArgsConstructor
public class PostViewKafkaListener extends AbstractKafkaListener<PostViewedEventDto> {

    @KafkaListener(topics = "${kafka.topics.post_view.name}", groupId = "${kafka.consumer.group-id}")
    public void listen(ConsumerRecord<String, Object> message) {
        consume(message, PostViewedEventDto.class, (this::handlePostViewedEvent));
    }

    private void handlePostViewedEvent(PostViewedEventDto postViewedEventDto, KafkaKey kafkaKey) {
        if (kafkaKey == KafkaKey.SAVE) {
            System.out.println(postViewedEventDto.getPostId());
        }
    }
}
