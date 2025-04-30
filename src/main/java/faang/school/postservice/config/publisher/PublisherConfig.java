package faang.school.postservice.config.publisher;

import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.publisher.AbstractEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class PublisherConfig {

    @Bean
    public Map<EventType, AbstractEventPublisher<?>> eventPublisherMap(List<AbstractEventPublisher<?>> publishers) {
        return publishers.stream()
                .collect(Collectors.toMap(AbstractEventPublisher::getEventType,
                        Function.identity(),
                        (a, b) -> {
                            log.warn("Duplicate event types detected");
                            throw new IllegalStateException(String.format("Duplicate key %s", a));
                        }));
    }
}
