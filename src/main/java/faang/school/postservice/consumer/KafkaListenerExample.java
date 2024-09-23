package faang.school.postservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaListenerExample {

    @KafkaListener(topics = "posts", groupId = "group1")
    public void listener(String data) {
        log.info("Received message [{}] in group1", data);
    }
}
