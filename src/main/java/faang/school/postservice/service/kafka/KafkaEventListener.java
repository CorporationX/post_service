package faang.school.postservice.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventListener {

    @KafkaListener(topics = "${spring.kafka.topics.feed-topic}", groupId = "${spring.kafka.client-id}")
    public void consume(Object message) {


    }
}
