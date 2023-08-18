package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.redis.PostViewEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostViewEventService {
    public static final String POST_VIEWED_LOG_MESSAGE = "Post id={} was viewed by user id={} at {}";

    private final ObjectMapper objectMapper;
    private final PostViewEventPublisher postViewEventPublisher;
    private final ChannelTopic postViewTopic;

    public PostViewEventDto getPostViewEventDto(Long userId, Post post) {
        PostViewEventDto postViewEventDto = objectMapper.convertValue(post, PostViewEventDto.class);
        postViewEventDto.setCreatedAt(LocalDateTime.now());
        postViewEventDto.setUserId(userId);
        return postViewEventDto;
    }

    public void publishEventToChannel(PostViewEventDto postViewEventDto) {
        try {
            String postViewEvent = objectMapper.writeValueAsString(postViewEventDto);
            postViewEventPublisher.publish(postViewTopic.getTopic(), postViewEvent);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
        }

        log.info(POST_VIEWED_LOG_MESSAGE,
                postViewEventDto.getPostId(),
                postViewEventDto.getUserId(),
                postViewEventDto.getCreatedAt()
        );
    }
}
