package faang.school.postservice.service.event;

import faang.school.postservice.model.event.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
@Component
public class PostViewEventBuffer {
    private final BlockingDeque<Event> buffer = new LinkedBlockingDeque<>();

    public void add(Event event) {
        try {
            buffer.put(event);
        } catch (InterruptedException e) {
            log.error("Failed to add event: interrupted", e);
            Thread.currentThread().interrupt();
        }
    }

    public List<Event> flush() {
        List<Event> events = new ArrayList<>();
        buffer.drainTo(events);
        return events;
    }
}
