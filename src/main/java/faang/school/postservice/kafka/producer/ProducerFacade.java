package faang.school.postservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProducerFacade<T> {

    private final List<ProduceHandler<T>> produceHandlers;

    public void publish(T data) {
        produceHandlers.forEach(handler -> {
            handler.publish(data);
        });
    }
}
