package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.NewPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewPostEventPublisher implements MessagePublisher<NewPostEvent> {

    @Value("${spring.data.channels.new_post_channel.name}")
    private String channelTopic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final Map<String, NewTopic> topicMap;

    @Override
    public void publish(NewPostEvent event) {

        NewTopic newCommentTopic = topicMap.get(channelTopic);

        String message;
        try {
            message = objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Error serializing like post event", e);
        }

        kafkaTemplate.send(newCommentTopic.name(), message);
        log.info("Published new comment event to Kafka - {}: {}", newCommentTopic.name(), message);
    }
}
