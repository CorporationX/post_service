package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.kafka.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentEventProducer implements KafkaEventProducer<KafkaCommentEventDto> {

    @Value("${spring.kafka.topics.comment-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaCommentEventDto> kafkaTemplate;

    @Override
    public void sendMessage(KafkaCommentEventDto messageDto) {
        log.info("sending message with comment: {}", messageDto);
        kafkaTemplate.send(topic, messageDto);
    }

}