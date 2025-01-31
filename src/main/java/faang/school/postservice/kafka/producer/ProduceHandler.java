package faang.school.postservice.kafka.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class ProduceHandler<T> {

    protected abstract void publish(T entity);
}
