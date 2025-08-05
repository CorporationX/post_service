package faang.school.postservice.messaging.producer.kafka;

import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.messaging.producer.EventProducer;
import faang.school.postservice.messaging.producer.EventProducerService;
import faang.school.postservice.model.message.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PostEventProducerService — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 06.08.2025
 */
@Service
@RequiredArgsConstructor
public class PostEventProducerServiceImpl implements EventProducerService<PostViewDto> {
    private final List<EventProducer<PostViewDto>> producers;

    @Override
    public void produce(Event type, PostViewDto event) {
        producers.stream()
                .filter(producer -> producer.isApplicable(type))
                .forEach(producer -> producer.send(event));
    }
}
