package faang.school.postservice.service.kafka.consumers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostLikeConsumer implements KafkaConsumer {


    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.likeTopic.name}",
            groupId = "${spring.data.kafka.groupId}")
    public void listen(String message) {

    }
}