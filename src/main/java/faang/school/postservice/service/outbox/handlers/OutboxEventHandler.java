package faang.school.postservice.service.outbox.handlers;

import faang.school.postservice.model.outbox.OutboxEvent;

public interface OutboxEventHandler {
    /** eventType value stored in outbox_event.event_type, e.g. "postCreated" */
    String eventType();

    /** Deserialize + send to Kafka (or whatever transport) */
    void handle(OutboxEvent event) throws Exception;
}