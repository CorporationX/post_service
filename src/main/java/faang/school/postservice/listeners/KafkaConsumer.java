package faang.school.postservice.listeners;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = {"${kafka.topics.like_event},${kafka.topics.comment_event},${kafka.topics.post_event},${kafka.topics.post_view_event}"})
public class KafkaConsumer {
    @Value("${kafka.header_class_key}")
    private String headerKey;
    public void consume(ConsumerRecord<String, Object> record) {
        String topic = record.topic();
        String header = new String(record.headers().lastHeader(headerKey).value());
        Object message = record.value();
        try {
            Class<?> c = Class.forName(header);
            Object deserializedMessage = convertToClass(message, c);
            log.info("got message {}", deserializedMessage);
        } catch (ClassNotFoundException e){
            log.error("did not managed to find class to serialize",e);
            throw new RuntimeException("did not managed to find class to serialize");
        }
    }

    private <T> T convertToClass(Object message, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(message, clazz);
    }
}
