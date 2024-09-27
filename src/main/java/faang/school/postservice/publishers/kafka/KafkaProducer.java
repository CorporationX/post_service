package faang.school.postservice.publishers.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer {
    @Value("${kafka.header_class_key}")
    private String headerKey;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object message) {
        Message<Object> kafkaMessage = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(headerKey, message.getClass().getName())
                .build();
        kafkaTemplate.send(kafkaMessage);
        log.info("Message of class {} sent to topic {}", message.getClass().getName(), topic);
    }
}
