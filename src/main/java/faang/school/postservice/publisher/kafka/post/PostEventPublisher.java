package faang.school.postservice.publisher.kafka.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.post.PostEventDto;
import faang.school.postservice.publisher.kafka.AbstractEventKafkaPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventKafkaPublisher<PostEventDto> {

    public PostEventPublisher(KafkaTemplate<String, String> kafkaTemplate, UserServiceClient userServiceClient) {
        super(kafkaTemplate,userServiceClient);

    }
}
