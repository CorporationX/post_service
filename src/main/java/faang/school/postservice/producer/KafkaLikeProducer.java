package faang.school.postservice.producer;

import faang.school.postservice.model.event.LikeEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeProducer extends KafkaAbstractProducer<LikeEvent> {

    public KafkaLikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Value("${spring.data.kafka.topics.like_topic}")
    private String likeTopic;

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name(likeTopic).build();
    }

    public void send(LikeEvent like) {
        super.sendMessage(likeTopic, like);
    }
}
