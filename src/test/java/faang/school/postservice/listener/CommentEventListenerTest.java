package faang.school.postservice.listener;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.cache.UserCacheDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.Acknowledgment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentEventListenerTest {
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
    UserCacheRepository userCacheRepository;
    @Mock
    UserServiceClient userServiceClient;
    @Captor
    private ArgumentCaptor<UserCacheDto> userArgumentCaptor;

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

    UserDto userDto = UserDto.builder()
            .id(authorId)
            .username("Author Name")
            .build();

    @Test
    public void testSuccessfullyCommentEventListened() {
        when(userServiceClient.getUser(authorId)).thenReturn(ResponseEntity.ok(userDto));
        commentEventListener.handleCommentPublishEvent(commentEventDto, acknowledgment);

        verify(userCacheRepository, times(1)).save(userArgumentCaptor.capture());
        UserCacheDto savedUser = userArgumentCaptor.getValue();
        assertEquals(authorId, savedUser.id());

        verify(postCacheRepository, times(1)).incrementCommentCount(
                eq(postId));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testFailWhileRedisExceptionReturned() {
        when(userServiceClient.getUser(authorId)).thenReturn(ResponseEntity.ok(userDto));
        doThrow(new RuntimeException("Can't add a value in Redis"))
                .when(postCacheRepository)
                .incrementCommentCount(eq(postId));

        commentEventListener.handleCommentPublishEvent(commentEventDtoError, acknowledgment);
        verify(postCacheRepository, times(1)).incrementCommentCount(
                eq(incorrectPostId));
        verify(acknowledgment, never()).acknowledge();
    }
}