package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeService likeService;

    private List<Like> likes;
    private List<UserDto> userDtos;
    private Comment comment;

    @BeforeEach
    void setUp() {
        Post post = Post.builder().id(1L).build();
        comment = Comment.builder().id(1L).build();
        likes = List.of(
                Like.builder().id(1L).userId(1L).post(post).createdAt(LocalDateTime.now()).build(),
                Like.builder().id(2L).userId(2L).post(post).createdAt(LocalDateTime.now()).build()
        );
        userDtos = List.of(
                new UserDto(1L, "John", "john@example.com"),
                new UserDto(2L, "Jane", "jane@example.com")
        );
        ReflectionTestUtils.setField(likeService, "batchSize", 100);
    }

    @Test
    void testGetUsersByPostIdSuccess() {
        Long postId = 1L;
        when(likeRepository.findByPostId(postId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getUsersByPostId(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetUsersByPostIdException() {
        Long postId = 1L;
        when(likeRepository.findByPostId(postId)).thenReturn(List.of());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> likeService.getUsersByPostId(postId));

        assertEquals(ExceptionMessages.LIKE_NOT_FOUND_FOR_POST + ": " + postId, exception.getMessage());
    }

    @Test
    void testGetUsersByCommentIdSuccess() {
        Long commentId = 1L;
        likes = List.of(
                Like.builder().id(1L).userId(1L).comment(comment).createdAt(LocalDateTime.now()).build(),
                Like.builder().id(2L).userId(2L).comment(comment).createdAt(LocalDateTime.now()).build()
        );
        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getUsersByCommentId(commentId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testGetUsersByCommentIdException() {
        Long commentId = 1L;
        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> likeService.getUsersByCommentId(commentId));

        assertEquals(ExceptionMessages.LIKE_NOT_FOUND_FOR_COMMENT + ": " + commentId, exception.getMessage());
    }
}