package faang.school.postservice.service.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.event.LikeEventMapper;
import faang.school.postservice.messaging.publisher.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventPublisherService {
    private final LikeEventMapper likeEventMapper;
    private final LikeEventPublisher likeEventPublisher;

    public void submitEvent(LikeDto likeDto) {
        LikeEvent likeEvent = likeEventMapper.toLikeEvent(likeDto);
        likeEventPublisher.publish(likeEvent);
    }
}
