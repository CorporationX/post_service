package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DuplicateLikeException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    private static final Long VALID_USER_ID = 100L;
    private static final Long VALID_POST_ID = 1L;
    private static final Long VALID_COMMENT_ID = 1L;
    private static final Long VALID_LIKE_ID = 1L;
    private static final Long INVALID_USER_ID = 999L;
    private static final Long DIFFERENT_USER_ID = 200L;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private LikeService likeService;

    private Post post;
    private Comment comment;
    private Like like;
    private Like commentLike;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(VALID_POST_ID)
                .build();

        comment = Comment.builder()
                .id(VALID_COMMENT_ID)
                .build();

        like = Like.builder()
                .id(VALID_LIKE_ID)
                .userId(VALID_USER_ID)
                .post(post)
                .build();

        commentLike = Like.builder()
                .id(VALID_LIKE_ID)
                .userId(VALID_USER_ID)
                .comment(comment)
                .build();

        userDto = new UserDto(VALID_USER_ID, "testuser", "test@example.com");
    }

    @Test
    void addLikeToPost_ValidData_ShouldAddLike() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(postRepository.getByIdOrThrow(VALID_POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(VALID_POST_ID, VALID_USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like savedLike = invocation.getArgument(0);
            return Like.builder()
                    .id(VALID_LIKE_ID)
                    .userId(savedLike.getUserId())
                    .post(savedLike.getPost())
                    .build();
        });

        LikeDto result = likeService.addLikeToPost(VALID_POST_ID);

        assertNotNull(result);
        assertEquals(VALID_USER_ID, result.userId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void addLikeToPost_UserNotFound_ShouldThrowException() {
        when(userContext.getUserId()).thenReturn(INVALID_USER_ID);
        when(userServiceClient.getUser(INVALID_USER_ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(VALID_POST_ID));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void addLikeToPost_UserIdMismatch_ShouldThrowException() {
        UserDto mismatchedUserDto = new UserDto(DIFFERENT_USER_ID, "otheruser", "other@example.com");

        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(mismatchedUserDto);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(VALID_POST_ID));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void addLikeToPost_UserServiceThrowsNotFound_ShouldThrowEntityNotFoundException() {
        when(userContext.getUserId()).thenReturn(INVALID_USER_ID);
        when(userServiceClient.getUser(INVALID_USER_ID)).thenThrow(new EntityNotFoundException("User not found"));

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(VALID_POST_ID));
    }

    @Test
    void addLikeToPost_UserServiceThrowsOtherException_ShouldThrowRuntimeException() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class, () -> likeService.addLikeToPost(VALID_POST_ID));
    }

    @Test
    void addLikeToPost_AlreadyLiked_ShouldThrowDuplicateLikeException() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(postRepository.getByIdOrThrow(VALID_POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(VALID_POST_ID, VALID_USER_ID)).thenReturn(Optional.of(like));

        assertThrows(DuplicateLikeException.class, () -> likeService.addLikeToPost(VALID_POST_ID));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void removeLikeFromPost_ValidData_ShouldRemoveLike() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserIdOrThrow(VALID_POST_ID, VALID_USER_ID)).thenReturn(like);

        LikeDto result = likeService.removeLikeFromPost(VALID_POST_ID);

        assertNotNull(result);
        assertEquals(VALID_USER_ID, result.userId());
        verify(likeRepository).deleteByPostIdAndUserId(VALID_POST_ID, VALID_USER_ID);
    }

    @Test
    void removeLikeFromPost_DifferentUser_ShouldThrowForbiddenException() {
        Like differentUserLike = Like.builder()
                .id(VALID_LIKE_ID)
                .userId(DIFFERENT_USER_ID)
                .post(post)
                .build();

        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserIdOrThrow(VALID_POST_ID, VALID_USER_ID)).thenReturn(differentUserLike);

        assertThrows(ForbiddenException.class, () -> likeService.removeLikeFromPost(VALID_POST_ID));
        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void addLikeToComment_ValidData_ShouldAddLike() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(commentRepository.getByIdOrThrow(VALID_COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(VALID_COMMENT_ID, VALID_USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like savedLike = invocation.getArgument(0);
            return Like.builder()
                    .id(VALID_LIKE_ID)
                    .userId(savedLike.getUserId())
                    .comment(savedLike.getComment())
                    .build();
        });

        LikeDto result = likeService.addLikeToComment(VALID_COMMENT_ID);

        assertNotNull(result);
        assertEquals(VALID_USER_ID, result.userId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void addLikeToComment_UserNotFound_ShouldThrowException() {
        when(userContext.getUserId()).thenReturn(INVALID_USER_ID);
        when(userServiceClient.getUser(INVALID_USER_ID)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToComment(VALID_COMMENT_ID));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void addLikeToComment_UserIdMismatch_ShouldThrowException() {
        UserDto mismatchedUserDto = new UserDto(DIFFERENT_USER_ID, "otheruser", "other@example.com");

        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(mismatchedUserDto);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToComment(VALID_COMMENT_ID));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void addLikeToComment_AlreadyLiked_ShouldThrowDuplicateLikeException() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(commentRepository.getByIdOrThrow(VALID_COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(VALID_COMMENT_ID, VALID_USER_ID)).thenReturn(Optional.of(commentLike));

        assertThrows(DuplicateLikeException.class, () -> likeService.addLikeToComment(VALID_COMMENT_ID));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void removeLikeFromComment_ValidData_ShouldRemoveLike() {
        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserIdOrThrow(VALID_COMMENT_ID, VALID_USER_ID)).thenReturn(commentLike);

        LikeDto result = likeService.removeLikeFromComment(VALID_COMMENT_ID);

        assertNotNull(result);
        assertEquals(VALID_USER_ID, result.userId());
        verify(likeRepository).deleteByCommentIdAndUserId(VALID_COMMENT_ID, VALID_USER_ID);
    }

    @Test
    void removeLikeFromComment_DifferentUser_ShouldThrowForbiddenException() {
        Like differentUserLike = Like.builder()
                .id(VALID_LIKE_ID)
                .userId(DIFFERENT_USER_ID)
                .comment(comment)
                .build();

        when(userContext.getUserId()).thenReturn(VALID_USER_ID);
        when(userServiceClient.getUser(VALID_USER_ID)).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserIdOrThrow(VALID_COMMENT_ID, VALID_USER_ID)).thenReturn(differentUserLike);

        assertThrows(ForbiddenException.class, () -> likeService.removeLikeFromComment(VALID_COMMENT_ID));
        verify(likeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
    }
}
