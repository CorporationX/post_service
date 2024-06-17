package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeEventPublisherTest {
    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private final String likeChannelName = "likeChannel";

    @BeforeEach
    public void setUp() {
        likeEventPublisher = new LikeEventPublisher(redisTemplate);
        likeEventPublisher.setLikeChannelName(likeChannelName);
    }

    @Test
    public void testPublish() {
        LikeEvent likeEvent = LikeEvent.builder().postId(1L).build();
        likeEventPublisher.publish(likeEvent);

        verify(redisTemplate, times(1)).convertAndSend(likeChannelName, likeEvent);
    }
}