package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDataSenderImpl implements DataSender{
    private final KafkaTemplate<String, Object> kafkaTemplateJson;

    @Override
    public void send(String topic, Event event) {
        log.info("KafkaDataSenderImpl: preparing for sending event: {}", event.toString());
        kafkaTemplateJson.send(topic, event)
                .whenComplete((record, ex) -> {
                    if (ex == null) {
                        log.info("KafkaDataSenderImpl: successfully sent '{}' with id {}, topic {}, partition = {}, " +
                                        "offset ={}",
                                event.getClass().getSimpleName(),
                                event.getId(),
                                topic,
                                record.getRecordMetadata().partition(),
                                record.getRecordMetadata().offset());
                    } else {
                        log.warn("KafkaDataSenderImpl: {} with id {} has not been sent",
                                event.getClass().getSimpleName(), event.getId(), ex);
                    }
                });
    }
}
