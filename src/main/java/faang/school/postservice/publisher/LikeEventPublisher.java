package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.model.event.LikeEvent;

public interface LikeEventPublisher  {
    void publish(LikeEvent likeEvent) throws JsonProcessingException;
}
