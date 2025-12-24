package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostPublishEventDto;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class PostEventListenerTest {
    private final static long DEFAULT_ID = 1L;
    private final static long ANOTHER_DEFAULT_ID = 1L;
    private final static long INCORRECT_ID = 1124L;

    private final long authorId = DEFAULT_ID;
    private final long postId = DEFAULT_ID;
    private final long incorrectAuthorId = INCORRECT_ID;
    private final long subscriberId = ANOTHER_DEFAULT_ID;
    private final List<Long> subscribers = List.of(subscriberId);
    private final List<Long> emptySubscribers = Collections.emptyList();
    @InjectMocks
    private PostEventListener postEventListener;

    @Mock
    private FeedCacheRepository feedCacheRepository;

    @Mock
    private Acknowledgment acknowledgment;

    PostPublishEventDto postPublishEventDto = PostPublishEventDto.builder()
            .content("content")
            .authorId(authorId)
            .postId(postId)
            .subscriberIds(subscribers)
            .build();

    PostPublishEventDto postPublishEventDtoWithoutSubscribers = PostPublishEventDto.builder()
            .content("content")
            .authorId(authorId)
            .postId(postId)
            .subscriberIds(emptySubscribers)
            .build();

    PostPublishEventDto postPublishEventDtoNullSubscribers = PostPublishEventDto.builder()
            .content("content")
            .authorId(authorId)
            .postId(postId)
            .build();

    PostPublishEventDto postPublishEventDtoError = PostPublishEventDto.builder()
            .content("content")
            .authorId(incorrectAuthorId)
            .postId(postId)
            .subscriberIds(subscribers)
            .build();

    @Test
    public void testSuccessfullyPostEventListened() {
        postEventListener.handlePostPublishEvent(postPublishEventDto, acknowledgment);

        verify(feedCacheRepository, times(1)).save(
                eq(subscriberId),
                eq(postId),
                any(Instant.class));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testDoNothingWhenSubscribersIsEmpty() {
        postEventListener.handlePostPublishEvent(postPublishEventDtoWithoutSubscribers, acknowledgment);

        verifyNoInteractions(feedCacheRepository);
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    public void testDoNothingWhenSubscribersIsNull() {
        postEventListener.handlePostPublishEvent(postPublishEventDtoNullSubscribers, acknowledgment);

        verifyNoInteractions(feedCacheRepository);
        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    public void testFailWhileRedisExceptionReturned() {
        doThrow(new RuntimeException("Can't add a value in Redis"))
                .when(feedCacheRepository)
                .save(eq(subscriberId), eq(postId), any(Instant.class));

        postEventListener.handlePostPublishEvent(postPublishEventDtoError, acknowledgment);
        verify(feedCacheRepository, times(1)).save(eq(subscriberId),
                eq(postId),
                any(Instant.class));

        verify(acknowledgment, never()).acknowledge();
    }
}
