package faang.school.postservice.producer.post;

import faang.school.postservice.dto.publishable.fornewsfeed.FeedPostEvent;
import faang.school.postservice.producer.AbstractEventProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostProducer extends AbstractEventProducer<FeedPostEvent> {
    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                             @Value("${spring.data.kafka.topics.post}") String topic) {
        super(kafkaTemplate, topic);
    }

    @Override
    @Retryable(retryFor = {Exception.class}, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendEvent(FeedPostEvent event) {
        super.sendEvent(event);
    }

    @Override
    @Retryable(retryFor = {Exception.class}, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void sendEvent(FeedPostEvent event, String messageKey) {
        super.sendEvent(event, messageKey);
    }

    @Recover
    public void recover(Exception e, FeedPostEvent event) {
        log.error("Failed to send event {} after retries", event, e);
    }
}
