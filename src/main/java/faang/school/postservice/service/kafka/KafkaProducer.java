package faang.school.postservice.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Long> kafkaTemplate;

    @Value("${kafka.topic.ban_user}")
    private String banUserTopic;

    public void sendUsersToBan(Long userId) {
        kafkaTemplate.send(banUserTopic, userId);
    }
}
