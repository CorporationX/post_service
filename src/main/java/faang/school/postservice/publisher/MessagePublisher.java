package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.UserEvent;

public interface MessagePublisher {
    void publish(UserEvent userEvent);
}
