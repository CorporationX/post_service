package faang.school.postservice.producer;

import faang.school.postservice.model.event.PostViewEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer extends KafkaAbstractProducer<PostViewEvent> {

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Value("${spring.data.kafka.topics.post_view_topic}")
    private String postViewTopic;

    @Bean
    public NewTopic postViewTopic() {
        return TopicBuilder.name(postViewTopic).build();
    }

    public void send(PostViewEvent postViewEvent) {
        super.sendMessage(postViewTopic, postViewEvent);
    }
}
