package faang.school.postservice.kafka.dispatcher;

import faang.school.postservice.event.KafkaEvent;
import faang.school.postservice.kafka.handler.EventHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDispatcher {

    private final ApplicationContext applicationContext;

    private final Map<Class<? extends KafkaEvent>, EventHandler<? extends KafkaEvent>> handlers
            = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        registerHandlers();
        log.info("Registered {} event handlers", handlers.size());
    }

    private void registerHandlers() {
        String[] beanNames = applicationContext.getBeanNamesForType(EventHandler.class);

        for (String beanName : beanNames) {
            EventHandler<?> handler = (EventHandler<?>) applicationContext.getBean(beanName);

            ResolvableType resolvableType = ResolvableType
                    .forClass(EventHandler.class, handler.getClass());

            ResolvableType eventType = resolvableType.getGeneric(0);
            Class<?> resolvedEventType = eventType.resolve();

            if (resolvedEventType != null &&
                    KafkaEvent.class.isAssignableFrom(resolvedEventType)) {

                Class<? extends KafkaEvent> typedEventType =
                        (Class<? extends KafkaEvent>) resolvedEventType;

                handlers.put(typedEventType, handler);
                log.debug("Registered handler '{}' for event type: {}",
                        beanName, typedEventType.getSimpleName());
            } else {
                log.warn("Cannot resolve event type for handler: {}", beanName);
            }
        }
    }

    /**
     * Диспетчеризация события к соответствующему обработчику
     */
    @Retryable(
            retryFor = {
                    DataAccessException.class,
                    RedisConnectionFailureException.class
            },
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 500,
                    multiplier = 2.0,
                    maxDelay = 5000
            )
    )
    public void dispatch(KafkaEvent event) {
        Class<? extends KafkaEvent> eventType = event.getClass();
        EventHandler<KafkaEvent> handler = (EventHandler<KafkaEvent>) handlers.get(eventType);

        if (handler == null) {
            log.warn("No handler found for event type: {}", eventType.getSimpleName());
            throw new IllegalArgumentException(
                    String.format("No handler registered for event type: %s",
                            eventType.getName())
            );
        }
        log.info("Handling event {} [attempt]", eventType.getSimpleName());
        handler.handle(event);
    }

    @Recover
    public void recover(RuntimeException ex, KafkaEvent event) {
        log.error(
                "Failed to process event {} after retries: {}",
                event.getClass().getSimpleName(),
                event,
                ex
        );
        throw ex;
    }

    /**
     * Проверка наличия обработчика для типа события
     */
    public boolean hasHandlerFor(Class<? extends KafkaEvent> eventType) {
        return handlers.containsKey(eventType);
    }
}

