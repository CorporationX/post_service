package faang.school.postservice.producer.like;

import faang.school.postservice.event.kafka.LikeKafkaEvent;
import faang.school.postservice.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class LikeProducer extends AbstractKafkaProducer<LikeKafkaEvent> {

    @Value("${spring.data.topics.topic-settings.likes.name}")
    private String channelTopic;

    public LikeProducer(KafkaTemplate<String, Object> kafkaTemplate,
                            Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, topicMap);
    }


    @Override
    public String getTopic() {
        return channelTopic;
    }
}
