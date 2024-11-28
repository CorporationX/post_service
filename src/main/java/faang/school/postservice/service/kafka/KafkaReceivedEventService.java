package faang.school.postservice.service.kafka;

public interface KafkaReceivedEventService<E> {

    void receiveFromBroker(E event);
}
