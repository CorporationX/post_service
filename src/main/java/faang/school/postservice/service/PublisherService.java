package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.mapper.PostViewEventMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.redis.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final PostViewEventMapper eventMapper;
    private final PostViewEventPublisher postViewEventPublisher;
    private final UserContext userContext;

    public void publishPostViewEventToRedis(Post post) {
        PostViewEventDto postViewEvent = eventMapper.toDto(post);
        postViewEvent.setViewTime(LocalDateTime.now());
        postViewEvent.setUserId(userContext.getUserId());

        postViewEventPublisher.publish(postViewEvent);
    }
}
