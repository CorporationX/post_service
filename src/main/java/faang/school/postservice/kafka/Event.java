package faang.school.postservice.kafka;

import faang.school.postservice.kafka.events.EventType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Event {
    private final UUID id = UUID.randomUUID();
    private LocalDateTime createdAt = LocalDateTime.now();
    private EventType eventType;
}
