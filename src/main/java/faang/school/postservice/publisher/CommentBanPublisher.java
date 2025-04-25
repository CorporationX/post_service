package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentBanPublisher extends AbstractEventPublisher implements KafkaEventPublisher<List<Long>> {

    private final KafkaTopic kafkaTopic;

    public CommentBanPublisher(KafkaTemplate<String, String> kafkaTemplate,
                               ObjectMapper objectMapper,
                               KafkaTopic kafkaTopic) {
        super(kafkaTemplate, objectMapper);
        this.kafkaTopic = kafkaTopic;
    }

    @Override
    public void publish(List<Long> usersForBan) {
        sendMessage(usersForBan, kafkaTopic.userBan().name());
    }
}
