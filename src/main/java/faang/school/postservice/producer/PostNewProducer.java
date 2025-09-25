package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.PostNewEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Component
public class PostNewProducer extends KafkaProducer<PostNewEvent> {
    private final KafkaProperty kafkaProps;
    @Value("${post.followersBatch:1000}")
    private int followersBatch;

    public PostNewProducer(ObjectMapper objectMapper,
                           KafkaProperty kafkaProperty,
                           KafkaTemplate<String, String> kafkaTemplate) {
        super(objectMapper, kafkaTemplate);
        this.kafkaProps = kafkaProperty;
    }

    @Override
    public void sendEvent(PostNewEvent event) {
        log.info("Sending event of new post: postId = {}", event.getPostId());

        List<Long> followees = event.getFollowees();
        for (int i = 0; i < followees.size(); i += followersBatch) {
            int end = Math.min(i + followersBatch, followees.size());
            event.setFollowees(followees.subList(i, end));

            sendTo(kafkaProps.topic().postNew(), event);
        }
    }
}
