package faang.school.postservice.producer;

import faang.school.postservice.dto.avro.LikeCreateEventAvro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Продюсер для отправки ивентов, связанных с проставлением лайка на пост или комментарий
 *
 * @author Linempy
 * @since 12.12.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    @Value("${spring.kafka.topics.likes}")
    private String topicForLike;

    private final KafkaTemplate<String, LikeCreateEventAvro> kafkaTemplate;

    public void sendMessage(LikeCreateEventAvro event) {
        CompletableFuture<SendResult<String, LikeCreateEventAvro>> future = kafkaTemplate.send(topicForLike, event);

        future.whenComplete((success, failure) -> {
            if (failure == null) {
                log.info("Ивент был успешно отправлен в топик: {}", topicForLike);
            } else {
                log.warn("Ивент не был отправлен в топик: {}", topicForLike);
            }
        });
    }
}