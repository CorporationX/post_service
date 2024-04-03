package faang.school.postservice.kafka.consumers;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer implements KafkaConsumer {

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.likeTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {

    }
}
