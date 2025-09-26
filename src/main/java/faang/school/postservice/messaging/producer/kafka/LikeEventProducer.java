package faang.school.postservice.messaging.producer.kafka;

import faang.school.postservice.dto.like.LikeViewDto;
import faang.school.postservice.messaging.producer.EventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Kafka-Producer для отправки события о лайке
 * <p>
 * Использует {@link KafkaTemplate} для сериализации и публикации {@link LikeViewDto}
 *  * в топик, заданный в настройках {@code kafka.topics.like-post}.
 * </p>*
 *
 * @author andreyfomchenko
 * @since 26.09.2025
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class LikeEventProducer implements EventProducer<LikeViewDto> {

    @Value("${kafka.topics.like-post}")
    private String likeTopic;
    private final KafkaTemplate<String, LikeViewDto> kafkaTemplate;

    @Override
    public void send(LikeViewDto event) {
        log.info("user {} liked the post: {}",event.likeAuthorId(), event.postId());
        kafkaTemplate.send(likeTopic, event);
    }
}
