package faang.school.postservice.service.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.service.like.implementations.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeServiceImpl likeService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUsersByPostId() {
        long postId = 1L;

        Like like1 = Like.builder().userId(1L).post(new Post()).build();
        Like like2 = Like.builder().userId(2L).post(new Post()).build();
        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like1, like2));

        UserDto user1 = new UserDto(1L, "user1", "user1@example.com");
        UserDto user2 = new UserDto(2L, "user2", "user2@example.com");
        when(userServiceClient.getUsersByIds(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        List<UserDto> users = likeService.getUserLikedPost(postId);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).username());
        assertEquals("user2", users.get(1).username());
    }

    @Test
    void testGetUsersByPostId_whenNoLikes() {
        long postId = 1L;
        when(likeRepository.findByPostId(postId)).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> likeService.getUserLikedPost(postId)
        );

        assertEquals("Не найдено лайков для поста с id: " + postId, exception.getMessage());
    }

    @Test
    void testGetUsersByPostId_whenUsersNotFound() {
        long postId = 1L;
        Like like1 = Like.builder().userId(1L).post(new Post()).build();
        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like1));
        when(userServiceClient.getUsersByIds(List.of(1L))).thenReturn(List.of());

        List<UserDto> users = likeService.getUserLikedPost(postId);

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testGetUsersByPostId_thenThrowException() {
        long postId = 1L;
        Like like1 = Like.builder().userId(1L).post(new Post()).build();
        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like1));
        when(userServiceClient.getUsersByIds(List.of(1L)))
                .thenThrow(new RuntimeException("Пользовательский сервис недоступен"));

        assertThrows(RuntimeException.class, () -> likeService.getUserLikedPost(postId));
    }

    @Test
    void testGetUsersByCommentId() {
        long commentId = 1L;

        Like like1 = Like.builder().userId(1L).comment(new Comment()).build();
        Like like2 = Like.builder().userId(2L).comment(new Comment()).build();
        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of(like1, like2));

        UserDto user1 = new UserDto(1L, "user1", "user1@example.com");
        UserDto user2 = new UserDto(2L, "user2", "user2@example.com");
        when(userServiceClient.getUsersByIds(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        List<UserDto> users = likeService.getUserLikedComment(commentId);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).username());
        assertEquals("user2", users.get(1).username());
    }

    @Test
    void testGetUsersByCommentId_whenNoLikes() {
        long commentId = 1L;
        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> likeService.getUserLikedComment(commentId)
        );

        assertEquals("Не найдено лайков для комментария с id: " + commentId, exception.getMessage());
    }



    @Test
    void testGetUsersByCommentId_whenUsersNotFound() {
        long commentId = 1L;
        Like like = Like.builder().userId(1L).comment(new Comment()).build();
        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of(like));
        when(userServiceClient.getUsersByIds(List.of(1L))).thenReturn(List.of());

        List<UserDto> users = likeService.getUserLikedComment(commentId);

        assertNotNull(users);
        assertTrue(users.isEmpty());
    }

    @Test
    void testGetUsersByCommentId_thenThrowException() {
        long commentId = 1L;
        Like like = Like.builder().userId(1L).comment(new Comment()).build();
        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of(like));
        when(userServiceClient.getUsersByIds(List.of(1L)))
                .thenThrow(new RuntimeException("Пользовательский сервис недоступен"));

        assertThrows(RuntimeException.class, () -> likeService.getUserLikedComment(commentId));
    }

}