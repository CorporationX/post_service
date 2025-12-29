package faang.school.postservice.listener;

import faang.school.postservice.cache.repository.FeedCacheRepository;
import faang.school.postservice.event.PostEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostEventListenerTest {

    @Mock
    private FeedCacheRepository feedCacheRepository;
    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private PostEventListener postEventListener;

    @Test
    void testHandlePostReceivedEvent() {
        PostEvent postEvent = PostEvent.builder()
                .postId(4L)
                .createdAt(LocalDateTime.now())
                .followersIds(List.of(1L))
                .build();

        postEventListener.handlePostReceivedEvent(postEvent, acknowledgment);

        verify(feedCacheRepository).save(eq(postEvent.followersIds().get(0)), eq(postEvent.postId()), eq(postEvent.createdAt()));
        verify(acknowledgment).acknowledge();
    }
}