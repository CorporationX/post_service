package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostProducer
        extends AbstractKafkaProducer<PostEventKafka> {

    @Value(value = "${spring.kafka.topics.post}")
    private String topicPost;

    public void sendMessage(PostEventKafka postEventKafka) {
        sendMessage(postEventKafka, topicPost);
    }
}
