package faang.school.postservice.publisher;

import faang.school.postservice.dto.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikePostEventPublisher {
    private final AsyncLikePostEventPublisher asyncPublisher;

    public void publish(LikePostEvent event) {
        asyncPublisher.asyncPublish(event);
    }
}
