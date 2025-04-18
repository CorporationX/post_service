package faang.school.postservice.scheduler.post.view;

import faang.school.postservice.model.event.Event;
import faang.school.postservice.publisher.post.view.EventPublisher;
import faang.school.postservice.service.event.PostViewEventBuffer;
import faang.school.postservice.util.EventTypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostViewEventScheduler {
    private final PostViewEventBuffer postViewEventBuffer;
    private final List<EventPublisher<?>> publishers;

    private Map<Class<? extends Event>, List<EventPublisher<?>>> eventPublisherMap;

    @PostConstruct
    public void init() {
        eventPublisherMap = publishers.stream()
                .collect(Collectors.groupingBy(
                        publisher -> EventTypeUtils.validateAndCast(publisher.getEventType())
                ));
    }

    @Scheduled(cron = "${spring.kafka.producer.scheduler.cron}")
    public void processEvent(){
        postViewEventBuffer.flush()
                .forEach(this::publishToKafka);
    }

    @SuppressWarnings("unchecked")
    private <T extends Event> void publishToKafka(T event){
        eventPublisherMap.getOrDefault(event.getClass(), List.of())
                .forEach(publisher -> ((EventPublisher<T>) publisher).publish(event));
    }
}
