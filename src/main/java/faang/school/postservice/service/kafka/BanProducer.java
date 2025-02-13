package faang.school.postservice.service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BanProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String banUserTopicName;

    public void sendUsersToBan(Long userId) {
        kafkaTemplate.send(banUserTopicName, String.valueOf(userId));
        System.out.println("Sended: " + userId);
    }
}
