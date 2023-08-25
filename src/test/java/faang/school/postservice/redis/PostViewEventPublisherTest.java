package faang.school.postservice.redis;

import faang.school.postservice.dto.redis.PostEventDto;
import faang.school.postservice.service.redis.ObjectMapperWriter;
import faang.school.postservice.service.redis.PostEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostViewEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ChannelTopic channelTopic;
    @Mock
    private ObjectMapperWriter writer;
    @InjectMocks
    private PostEventPublisher postViewEventPublisher;

    @Test
    void testPublish() {
        PostEventDto dto = PostEventDto.builder()
                .postId(1L)
                .authorId(2L)
                .build();
        when(channelTopic.getTopic()).thenReturn("post_channel");
        postViewEventPublisher.publish(dto);
        verify(redisTemplate).convertAndSend(any(), any());
    }
}
