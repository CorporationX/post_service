package faang.school.postservice.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventProducer<T> {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async("executor")
    public void sendMessage(T event, String channelTopic) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(channelTopic, event);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Sent message=[" + event +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            } else {
                log.error("Unable to send message=[" +
                        event + "] due to : " + ex.getMessage());
            }
        });
    }

}
