package faang.school.postservice.publisher.kafka.postview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.publisher.kafka.AbstractEventPublisher;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostViewsEventPublisher extends AbstractEventPublisher<PostEvent> {

    public PostViewsEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper,@Qualifier("postEventTopic") NewTopic postEventTopic){
        super(kafkaTemplate,objectMapper,postEventTopic);
    }
}
