package faang.school.postservice.listener;

import faang.school.postservice.dto.cache.PostCacheDto;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.cache.UserCacheDto;
import faang.school.postservice.dto.event.PostPublishEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.cache.FeedCacheRepository;
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

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
    private PostCacheRepository postCacheRepository;

    @Mock
    private UserCacheRepository userCacheRepository;

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private Acknowledgment acknowledgment;

    @Captor
    private ArgumentCaptor<PostCacheDto> postArgumentCaptor;
    @Mock
    private UserServiceClient userServiceClient;

    @Captor
    private ArgumentCaptor<UserCacheDto> userArgumentCaptor;


    UserDto userDto = UserDto.builder()
            .id(authorId)
            .username("Author Name")
            .build();

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

    PostCacheDto postCacheDto = PostCacheDto.builder()
            .id(postId)
            .authorId(authorId)
            .content("content")
            .createdAt(Instant.now())
            .build();

    @Test
    public void testSuccessfullyPostEventListened() {
        when(userServiceClient.getUser(authorId)).thenReturn(ResponseEntity.ok(userDto));
        postEventListener.handlePostPublishEvent(postPublishEventDto, acknowledgment);

        verify(postCacheRepository, times(1)).save(postArgumentCaptor.capture());

        PostCacheDto savedPost = postArgumentCaptor.getValue();
        assertEquals(postId, savedPost.id());
        assertEquals(authorId, savedPost.authorId());
        assertEquals("content", savedPost.content());
        assertNotNull(savedPost.createdAt());
        verify(userCacheRepository, times(1)).save(userArgumentCaptor.capture());

        UserCacheDto savedUser = userArgumentCaptor.getValue();
        assertEquals(authorId, savedUser.id());

        verify(feedCacheRepository, times(1)).save(
                eq(subscriberId),
                eq(postId),
                any(Instant.class));
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testDoNothingWhenSubscribersIsEmpty() {
        when(userServiceClient.getUser(authorId)).thenReturn(ResponseEntity.ok(userDto));
        postEventListener.handlePostPublishEvent(postPublishEventDtoWithoutSubscribers, acknowledgment);

        verifyNoInteractions(feedCacheRepository);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testDoNothingWhenSubscribersIsNull() {
        when(userServiceClient.getUser(authorId)).thenReturn(ResponseEntity.ok(userDto));
        postEventListener.handlePostPublishEvent(postPublishEventDtoNullSubscribers, acknowledgment);

        verifyNoInteractions(feedCacheRepository);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    public void testFailWhileRedisExceptionReturned() {
        when(userServiceClient.getUser(incorrectAuthorId)).thenReturn(ResponseEntity.ok(userDto));
        doThrow(new RuntimeException("Can't add a value in Redis"))
                .when(feedCacheRepository)
                .save(eq(subscriberId), eq(postId), any(Instant.class));

        postEventListener.handlePostPublishEvent(postPublishEventDtoError, acknowledgment);
        verify(feedCacheRepository, times(1)).save(eq(subscriberId),
                eq(postId),
                any(Instant.class));

        verify(acknowledgment, never()).acknowledge();
    }

    @Test
    public void testFailWhilePostRedisExceptionReturned() {
        doThrow(new RuntimeException("Can't add a value in Post Redis"))
                .when(postCacheRepository)
                .save(any(PostCacheDto.class));

        postEventListener.handlePostPublishEvent(postPublishEventDtoError, acknowledgment);
        verify(postCacheRepository, times(1)).save(any(PostCacheDto.class));
        verifyNoInteractions(feedCacheRepository);
        verify(acknowledgment, never()).acknowledge();
    }
    
    @Test  
    public void testFailWhileUserRedisExceptionReturned() {
        when(userServiceClient.getUser(incorrectAuthorId)).thenReturn(ResponseEntity.ok(userDto));
        doThrow(new RuntimeException("Can't add a value in User Redis"))
                .when(userCacheRepository)
                .save(any(UserCacheDto.class));

        postEventListener.handlePostPublishEvent(postPublishEventDtoError, acknowledgment);
        verify(userCacheRepository, times(1)).save(any(UserCacheDto.class));
        verifyNoInteractions(feedCacheRepository);
        verify(acknowledgment, never()).acknowledge();
    }
}
