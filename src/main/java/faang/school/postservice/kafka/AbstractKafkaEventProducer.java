package faang.school.postservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
public abstract class AbstractKafkaEventProducer<T> {

    protected String getTopic() {
        return null;
    }

    protected KafkaTemplate<String, T> getKafkaTemplate() {
        return null;
    }

    public void sendMessage(T messageDto) {
        if (getTopic() == null || getKafkaTemplate() == null) {
            log.info("No topic: {} or kafka template: {} was found.", getTopic(), getKafkaTemplate());
            return;
        }
        log.info("Sending message: {} | with topic: {}", messageDto, getTopic());
        getKafkaTemplate().send(getTopic(), messageDto);
    }
}
