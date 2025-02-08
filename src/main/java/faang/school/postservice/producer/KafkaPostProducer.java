package faang.school.postservice.producer;

import faang.school.postservice.model.event.PostEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer extends KafkaAbstractProducer<PostEvent> {

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Value("${spring.data.kafka.topics.post_topic}")
    private String postTopic;

    @Bean
    public NewTopic postTopic() {
        return TopicBuilder.name(postTopic).build();
    }

    public void send(PostEvent post) {
        super.sendMessage(postTopic, post);
    }
}
