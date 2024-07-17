package faang.school.postservice.producer.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PostViewProducer extends AbstractKafkaProducer<PostViewKafkaEvent> {

    @Value("${spring.data.topics.topic-settings.post-views.name}")
    private String channelTopic;

    public PostViewProducer(KafkaTemplate<String, String> kafkaTemplate,
                        ObjectMapper objectMapper,
                        Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, objectMapper, topicMap);
    }

    @Override
    public String getTopic() {
        return channelTopic;
    }
}
