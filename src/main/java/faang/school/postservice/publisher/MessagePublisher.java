package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.model.event.LikeEvent;

public interface MessagePublisher {
    public void publish(String message);
}
