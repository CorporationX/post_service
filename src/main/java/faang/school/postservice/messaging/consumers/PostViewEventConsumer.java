package faang.school.postservice.messaging.consumers;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class PostViewEventConsumer {

    @KafkaListener(topics = "post-view")
    public void listen(PostViewEvent event) {

    }
}
