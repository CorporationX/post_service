package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPublisher <T>{
    private final String topic;
    private final KafkaTemplate<String, T> kafkaTemplate;

    public void sendMessage(T msg) {

        kafkaTemplate.send(topic, msg);
    }

    //using Completable future to ensure that the message was delivered

}
