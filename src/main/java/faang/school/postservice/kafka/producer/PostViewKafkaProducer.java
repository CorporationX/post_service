package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PostViewKafkaProducer extends AbstractKafkaProducer<PostViewKafkaEvent> {

    @Value("${kafka.topics.post-view}")
    private String postViewKafkaTopic;

    public PostViewKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper mapper) {
        super(kafkaTemplate, mapper);
    }

    @Override
    protected String getTopic() {
        return postViewKafkaTopic;
    }
}

