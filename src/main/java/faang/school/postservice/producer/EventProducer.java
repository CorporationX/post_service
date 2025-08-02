package faang.school.postservice.producer;

public interface EventProducer<T> {

    void publish(T eventDto);

    String getTopic();
}
