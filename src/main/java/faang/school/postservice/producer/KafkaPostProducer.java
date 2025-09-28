package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.PostPublishedEventAvro;
import faang.school.postservice.publisher.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Продюсер для отправки ивентов в топик {@code posts}
 *
 * @author Linempy
 * @since 22.09.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostProducer implements EventProducer<PostPublishedEventAvro> {

    @Value("${spring.kafka.topics.posts}")
    private String topicForPosts;

    private final KafkaTemplate<String, PostPublishedEventAvro> kafkaTemplate;

    @Override
    public void sendMessage(PostPublishedEventAvro event) {
        CompletableFuture<SendResult<String, PostPublishedEventAvro>> future = kafkaTemplate.send(topicForPosts, event);

        future.whenComplete((success, failure) -> {
            if (failure == null) {
                log.info("Ивент был успешно отправлен в топик: {}", topicForPosts);
            } else {
                log.warn("Ивент не был отправлен в топик: {}", topicForPosts);
            }
        });
    }

}