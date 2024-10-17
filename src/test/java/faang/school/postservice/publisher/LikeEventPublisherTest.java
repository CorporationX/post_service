package faang.school.postservice.publisher;

import faang.school.postservice.model.event.LikeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
class LikeEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private LikeEventPublisher likeEventPublisher;

    private LikeEvent likeEvent;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        likeEvent = LikeEvent.builder()
                .postId(1L)
                .build();

        Field field = likeEventPublisher.getClass().getDeclaredField("likeTopic");
        field.setAccessible(true);
        field.set(likeEventPublisher, "test_like_channel");
    }

    @Test
    void publisher() {
        //when
        likeEventPublisher.publish(likeEvent);

        //then
        Mockito.verify(redisTemplate, Mockito.times(1))
                .convertAndSend(Mockito.eq("test_like_channel"), Mockito.eq(likeEvent));
    }
}