package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.like.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private LikeEventMapper likeEventMapper;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private LikeServiceImpl likeService;

    Post post;
    Like like;
    Long userId;
    Long postId;
    Long authorId;
    LocalDateTime nullDate;
    String jsonEvent;
    LikeEvent likeEvent;

    @BeforeEach
    public void setup() {
        userId = 1L;
        postId = 2L;
        authorId = 3L;
        nullDate = null;

        post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .likes(List.of())
                .build();

        like = Like.builder()
                .userId(userId)
                .post(post)
                .createdAt(nullDate)
                .build();

        likeEvent = new LikeEvent(2L, 3L, 1L, LocalDateTime.now());

        // если date будет не null.
        // То ФОРМАТ даты в стринге jsonEvent будет отличаться от даты получаемой в test_addLikePost_publish_successful
        jsonEvent = String.format("{\"postId\":%d,\"authorId\":%d,\"userId\":%d,\"createdAt\":%s}",
                postId, authorId, userId, nullDate);
    }

    @Test
    public void test_addLikePost_publish_successful() throws JsonProcessingException {
        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(likeRepository.save(any())).thenReturn(like);
        when(likeEventMapper.likeToEvent(like)).thenReturn(likeEvent);
        when(objectMapper.writeValueAsString(likeEvent)).thenReturn(jsonEvent);

        likeService.addLikePost(postId, userId);

        verify(likeEventPublisher, times(1)).publish(jsonEvent);
        verifyNoMoreInteractions(likeEventPublisher);
    }

    @Test
    public void test_addLikePost_publish_throwsJsonProcessingException() throws JsonProcessingException {
        Like savedLike = Like.builder().id(100L).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(userId)).thenReturn(new UserDto(userId, "name", "email"));
        when(likeRepository.save(any())).thenReturn(savedLike);
        when(likeEventMapper.likeToEvent(savedLike)).thenReturn(likeEvent);
        when(objectMapper.writeValueAsString(likeEvent))
                .thenThrow(new JsonProcessingException("Test error") {
                });

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> likeService.addLikePost(postId, userId));

        assertInstanceOf(JsonProcessingException.class, exception.getCause());
        verify(likeEventPublisher, never()).publish(anyString());
    }
}
