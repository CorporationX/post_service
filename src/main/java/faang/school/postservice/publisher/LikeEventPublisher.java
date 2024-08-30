package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.redisEvent.LikeEvent;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements EventPublisher<LikeEvent>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventTopic;
    private final PostService service;
    private final ObjectMapper objectMapper;
    private final LikeEventMapper likeEventMapper;

    public void validator(LikeDto eventDto, Long postId) {
        PostDto post = service.getPost(postId);
        LikeEvent likeEvent = likeEventMapper.toEntity(eventDto);
        likeEvent.setAuthorId(post.getAuthorId());
        publish(likeEvent);
    }

    @Override
    public void publish(LikeEvent event) {
        try {
            String message =  objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(likeEventTopic.getTopic(), message);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
