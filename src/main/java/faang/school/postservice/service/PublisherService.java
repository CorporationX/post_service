package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.redis.PostEventDto;
import faang.school.postservice.mapper.PostEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.redis.PostEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PostEventMapper eventMapper;
    private final PostEventPublisher postEventPublisher;
    private final UserContext userContext;

    public void publishPostEventToRedis(Post post) {
        PostEventDto postEvent = eventMapper.toDto(post);
        postEvent.setViewTime(LocalDateTime.now());
        postEvent.setUserId(userContext.getUserId());

        postEventPublisher.publish(postEvent);
    }
}
