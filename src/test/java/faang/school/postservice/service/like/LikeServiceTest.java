package faang.school.postservice.service.like;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.mapper.comment.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.UserClientValidation;
import faang.school.postservice.validator.like.CommentLikeValidation;
import faang.school.postservice.validator.like.PostLikeValidation;
import jakarta.persistence.PersistenceException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserClientValidation userClientValidation;

    @Mock
    private PostLikeValidation postLikeValidation;

    @Mock
    private CommentLikeValidation commentLikeValidation;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private CommentService commentService;

    @Mock
    private PostService postService;

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

    @Test
    void testAddLikeToPost() {
        doNothing().when(userClientValidation).checkUser(1L);
        doNothing().when(postLikeValidation).validateLikes(1L, 1L);
        when(likeRepository.save(any(Like.class))).thenReturn(getLike());
        when(likeMapper.toDto(getLike())).thenReturn(getLikeDto());

        LikeDto likeDto = likeService.addLikeToPost(1L, getLikeDto());

        verify(userClientValidation).checkUser(1L);
        verify(postLikeValidation).validateLikes(1L, 1L);
        verify(likeMapper).toDto(any(Like.class));
        verify(likeRepository).save(any(Like.class));
        assertNotNull(likeDto);
    }

    @Test
    void testAddLikeToComment() {
        doNothing().when(userClientValidation).checkUser(1L);
        doNothing().when(commentLikeValidation).validateLikes(1L, 1L);
        when(likeRepository.save(any(Like.class))).thenReturn(getLike());
        when(likeMapper.toDto(getLike())).thenReturn(getLikeDto());

        LikeDto likeDto = likeService.addLikeToComment(1L, getLikeDto());

        verify(userClientValidation).checkUser(1L);
        verify(commentLikeValidation).validateLikes(1L, 1L);
        verify(likeMapper).toDto(any(Like.class));
        verify(likeRepository).save(any(Like.class));
        assertNotNull(likeDto);
    }

    @Test
    void testExceptionOnAddLikeToPost() {
        doNothing().when(userClientValidation).checkUser(1L);
        doNothing().when(postLikeValidation).validateLikes(1L, 1L);
        when(likeRepository.save(getLike())).thenThrow(new PersistenceException("error"));
        when(likeMapper.toDto(any(Like.class))).thenReturn(getLikeDto());

        assertThrows(PersistenceException.class, () -> likeService.addLikeToPost(1L, getLikeDto()));

    }

    @Test
    void testExceptionOnAddLikeToComment() {
        doNothing().when(userClientValidation).checkUser(1L);
        doNothing().when(commentLikeValidation).validateLikes(1L, 1L);
        when(likeRepository.save(getLike())).thenThrow(new PersistenceException());
        when(likeMapper.toDto(getLike())).thenReturn(getLikeDto());

        assertThrows(PersistenceException.class, () -> likeService.addLikeToComment(1L, getLikeDto()));
    }

    @Test
    void testRemoveLikeFromPost() {
        doNothing().when(likeRepository).deleteByPostIdAndUserId(1L, 1L);
        doNothing().when(postLikeValidation).checkExistRecord(1L);

        assertDoesNotThrow(() -> likeService.removeLikeFromPost(1L, getLikeDto()));
        verify(likeRepository).deleteByPostIdAndUserId(1L, 1L);
        verify(postLikeValidation).checkExistRecord(1L);
    }

    @Test
    void testRemoveLikeFromComment() {
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(1L, 1L);
        doNothing().when(commentLikeValidation).checkExistRecord(1L);

        assertDoesNotThrow(() -> likeService.removeLikeFromComment(1L, getLikeDto()));
        verify(likeRepository).deleteByCommentIdAndUserId(1L, 1L);
        verify(commentLikeValidation).checkExistRecord(1L);
    }

    @Test
    public void testExceptionOnRemoveLikeFromPost() {
        doThrow(new RuntimeException("error")).when(likeRepository).deleteByPostIdAndUserId(1L, 1L);
        assertThatThrownBy(() -> likeService.removeLikeFromPost(1L, getLikeDto()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("error");
    }

    @Test
    public void testExceptionOnRemoveLikeFromComment() {
        doThrow(new RuntimeException("error")).when(likeRepository).deleteByCommentIdAndUserId(1L, 1L);
        assertThatThrownBy(() -> likeService.removeLikeFromComment(1L, getLikeDto()))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("error");
    }

    private static LikeDto getLikeDto() {
        return LikeDto.builder()
            .id(1L)
            .userId(1L)
            .commentId(1L)
            .postId(1L)
            .createdAt(LocalDateTime.of(2024, Month.AUGUST, 24,0, 0))
            .build();
    }

    private static Like getLike() {
        return Like.builder()
            .id(1)
            .userId(1L)
            .comment(Comment.builder()
                .id(1L)
                .build())
            .post(Post.builder()
                .id(1L)
                .build())
            .createdAt(LocalDateTime.of(2024, Month.AUGUST, 24,0, 0))
            .build();
    }
}