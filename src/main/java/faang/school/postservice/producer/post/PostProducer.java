package faang.school.postservice.producer.post;

import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.producer.AbstractProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PostProducer extends AbstractProducer<PostEvent> {
    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${kafka.topic.posts-topic.name}") String topicName) {
        super(kafkaTemplate, topicName);
    }
}

