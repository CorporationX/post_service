package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.Event;

public interface DataSender {
    void send(String topic, Event event);
}
