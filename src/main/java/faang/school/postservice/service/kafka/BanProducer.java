package faang.school.postservice.service.kafka;

import faang.school.event.UserBanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BanProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUsersToBan(String topic, UserBanEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
