package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostCreatedEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCreatedKafkaPublisher {
    private final PostCreatedKafkaProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PostCreatedEventDto event) {
        producer.send(event);
    }
}