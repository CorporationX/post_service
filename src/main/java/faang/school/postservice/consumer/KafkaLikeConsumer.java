package faang.school.postservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import org.apache.kafka.clients.admin.NewTopic;

import org.springframework.stereotype.Component;

@Component
public class KafkaLikeConsumer extends AbstractKafkaConsumer<String, LikeEvent> {
    
    public KafkaLikeConsumer(
        NewTopic likesTopic,
        String groupId,
        ObjectMapper objectMapper
    ) {
        super(likesTopic.name(), groupId, objectMapper);
    }

    @Override
    protected void processMessage(String key, LikeEvent message) {

    }

    @Override
    protected Class<LikeEvent> getValueType() {
        return LikeEvent.class;
    }
}
