package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class HeatKafkaPublisher extends AbstractKafkaPublisher<Long> {
    @Value("${kafka.topics.heat.name}")
    private String heatTopic;

    public HeatKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, UserServiceClient userServiceClient, UserServiceClient userServiceClient1, PostKafkaPublisher postKafkaPublisher) {
        super(kafkaTemplate);
    }

    public void publish(long id) {
        send(heatTopic, id);
    }
}
