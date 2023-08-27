package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.PostViewEventPublisher;
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
    private final ProjectServiceClient projectServiceClient;

    public PostViewEventDto getPostViewEventDto(Long userId, Post post) {
        PostViewEventDto postViewEventDto = objectMapper.convertValue(post, PostViewEventDto.class);

        postViewEventDto.setCreatedAt(LocalDateTime.now());
        postViewEventDto.setUserId(userId);
        postViewEventDto.setAuthorId(getAuthorId(post));
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

    private Long getAuthorId(Post post) {
        return (post.getProjectId() != null)
                ? projectServiceClient.getProject(post.getProjectId()).getOwnerId()
                : post.getAuthorId();
    }
}
