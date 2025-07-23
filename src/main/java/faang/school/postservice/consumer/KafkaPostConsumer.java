package faang.school.postservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
    @KafkaListener(topics = "${kafka.topics.posts}")
    public void consume( event) {

    }
}
