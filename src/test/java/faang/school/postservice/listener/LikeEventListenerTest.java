package faang.school.postservice.listener;

import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.repository.cache.PostCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeEventListenerTest {
    private final static long DEFAULT_ID = 1L;
    private final static long ANOTHER_DEFAULT_ID = 4L;
    private final static long INCORRECT_ID = 1124L;

    private final long likeId = ANOTHER_DEFAULT_ID;
    private final long authorId = DEFAULT_ID;
    private final long postId = DEFAULT_ID;
    private final long incorrectPostId = INCORRECT_ID;


    @InjectMocks
    LikeEventListener likeEventListener;

    @Mock
    PostCacheRepository postCacheRepository;

    @Mock
    Acknowledgment acknowledgment;

    LikeEventDto likeEventDto = LikeEventDto.builder()
            .likeId(likeId)
            .postId(postId)
            .authorId(authorId)
            .build();

    LikeEventDto likeEventDtoError = LikeEventDto.builder()
            .likeId(likeId)
            .postId(incorrectPostId)
            .authorId(authorId)
            .build();

    @Test
    public void testSuccessfullyLikeEventListened() {
        likeEventListener.handleLikeSetEvent(likeEventDto, acknowledgment);

        verify(postCacheRepository, times(1)).incrementLikeCount(
                eq(postId));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testFailWhileRedisExceptionReturned() {
        doThrow(new RuntimeException("Can't add a value in Redis"))
                .when(postCacheRepository)
                .incrementLikeCount(eq(postId));

        likeEventListener.handleLikeSetEvent(likeEventDtoError, acknowledgment);
        verify(postCacheRepository, times(1)).incrementLikeCount(
                eq(incorrectPostId));
        verify(acknowledgment, never()).acknowledge();
    }
}
