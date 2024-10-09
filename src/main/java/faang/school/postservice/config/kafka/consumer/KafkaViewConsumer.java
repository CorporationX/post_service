package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.dto.post.PostViewDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaViewConsumer {

    @KafkaListener(topics = "view_theme", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(PostViewDto event,
                       Acknowledgment acknowledgment) {
        log.info(event.toString());

        acknowledgment.acknowledge();
    }
}
