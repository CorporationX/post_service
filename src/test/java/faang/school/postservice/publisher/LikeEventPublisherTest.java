package faang.school.postservice.publisher;

import faang.school.postservice.model.dto.like.LikeEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Test
    @DisplayName("Send Event Test")
    void testSendEvent() {
        var likeEvent = LikeEventDto.builder()
                .postId(1)
                .build();

        likeEventPublisher.sendEvent(likeEvent);
        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), likeEvent);
    }
}