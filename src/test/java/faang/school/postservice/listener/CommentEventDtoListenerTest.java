package faang.school.postservice.listener;

import faang.school.postservice.dto.comment.CommentEventDto;
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
public class CommentEventDtoListenerTest {
    private final static long DEFAULT_ID = 1L;
    private final static long ANOTHER_DEFAULT_ID = 4L;
    private final static long INCORRECT_ID = 1124L;
    private final static String CONTENT = "comment content";

    private final long commentId = ANOTHER_DEFAULT_ID;
    private final long authorId = DEFAULT_ID;
    private final long postId = DEFAULT_ID;
    private final long incorrectPostId = INCORRECT_ID;
    private final String commentContent = CONTENT;


    @InjectMocks
    CommentEventListener commentEventListener;

    @Mock
    PostCacheRepository postCacheRepository;

    @Mock
    Acknowledgment acknowledgment;

    CommentEventDto commentEventDto = CommentEventDto.builder()
            .commentId(commentId)
            .postId(postId)
            .authorId(authorId)
            .content(commentContent)
            .build();

    CommentEventDto commentEventDtoError = CommentEventDto.builder()
            .commentId(commentId)
            .postId(incorrectPostId)
            .authorId(authorId)
            .content(commentContent)
            .build();

    @Test
    public void testSuccessfullyCommentEventListened() {
        commentEventListener.handleCommentPublishEvent(commentEventDto, acknowledgment);

        verify(postCacheRepository, times(1)).incrementCommentCount(
                eq(postId));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testFailWhileRedisExceptionReturned() {
        doThrow(new RuntimeException("Can't add a value in Redis"))
                .when(postCacheRepository)
                .incrementCommentCount(eq(postId));

        commentEventListener.handleCommentPublishEvent(commentEventDtoError, acknowledgment);
        verify(postCacheRepository, times(1)).incrementCommentCount(
                eq(incorrectPostId));
        verify(acknowledgment, never()).acknowledge();
    }
}