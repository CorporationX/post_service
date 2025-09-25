package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.PostPublishedEventAvro;
import faang.school.postservice.publisher.EventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Продюсер для отправки ивентов в топик {@code posts}
 *
 * @author Linempy
 * @since 22.09.2025
 */
@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements EventProducer<PostPublishedEventAvro> {

    @Value("${kafka.topics.posts}")
    private String topicForPosts;

    private final KafkaTemplate<String, PostPublishedEventAvro> kafkaTemplate;

    @Override
    public void sendMessage(PostPublishedEventAvro event) {
        kafkaTemplate.send(topicForPosts, event);
    }
}