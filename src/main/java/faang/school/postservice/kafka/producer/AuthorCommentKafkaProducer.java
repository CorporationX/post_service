package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.AuthorCommentKafkaEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthorCommentKafkaProducer extends AbstractKafkaProducer<AuthorCommentKafkaEvent> {

    @Value("${kafka.topics.author-of-comment}")
    private String authorOfCommentKafkaTopic;

    public AuthorCommentKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Override
    protected String getTopic() {
        return authorOfCommentKafkaTopic;
    }
}

