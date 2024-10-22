package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface MessagePublisher {
    void publish(Object message) throws JsonProcessingException;
}
