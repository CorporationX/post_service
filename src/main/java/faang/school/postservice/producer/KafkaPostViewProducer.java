package faang.school.postservice.producer;

import faang.school.postservice.dto.event.ViewEventKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewProducer
        extends AbstractKafkaProducer<ViewEventKafka>{

    @Value(value = "${spring.kafka.topics.view}")
    private String topicView;

    public void sendMessage(ViewEventKafka viewEventKafka) {
        sendMessage(viewEventKafka, topicView);
    }
}
