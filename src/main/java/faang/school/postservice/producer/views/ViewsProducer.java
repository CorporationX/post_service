package faang.school.postservice.producer.views;

import faang.school.postservice.event.ViewsEvent;
import faang.school.postservice.producer.AbstractProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ViewsProducer extends AbstractProducer<ViewsEvent> implements ViewsServiceProducer {
    public ViewsProducer(KafkaTemplate<String, Object> kafkaTemplate,
                         @Value("${kafka.topic.views-topic.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }

    @Override
    public void send(Long postId) {
        ViewsEvent event = new ViewsEvent(postId);
        sendEvent(event);
    }
}
