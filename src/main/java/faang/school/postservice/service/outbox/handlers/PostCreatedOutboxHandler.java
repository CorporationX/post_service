package faang.school.postservice.service.outbox.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostCreatedEventDto;
import faang.school.postservice.model.outbox.OutboxEvent;
import faang.school.postservice.publisher.PostCreatedKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostCreatedOutboxHandler implements OutboxEventHandler {

    private final String eventType;
    private final ObjectMapper objectMapper;
    private final PostCreatedKafkaProducer producer;

    public PostCreatedOutboxHandler(
            @Value("${app.outbox.handlers.post-created.event-type}") String eventType,
            ObjectMapper objectMapper,
            PostCreatedKafkaProducer producer
    ) {
        this.eventType = eventType;
        this.objectMapper = objectMapper;
        this.producer = producer;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public void handle(OutboxEvent event) throws Exception {
        PostCreatedEventDto dto =
                objectMapper.readValue(event.getPayload(), PostCreatedEventDto.class);

        producer.send(dto);
    }
}
