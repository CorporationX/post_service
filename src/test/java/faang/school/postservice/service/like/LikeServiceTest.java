package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 2L;
    private static final Long USER_ID = 100L;
    private static final Long OTHER_USER_ID = 200L;
    private static final Long LIKE_ID = 50L;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    private Post post;
    private Comment comment;
    private Like like;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(POST_ID)
                .build();

        comment = Comment.builder()
                .id(COMMENT_ID)
                .build();

        like = Like.builder()
                .id(LIKE_ID)
                .userId(USER_ID)
                .post(post)
                .comment(null)
                .build();
    }

    @Test
    void addLikeToPost_ValidData_ShouldAddLike() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(postRepository.getByIdOrThrow(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        likeService.addLikeToPost(POST_ID, USER_ID);

        verify(likeRepository).save(any(Like.class));
        verify(userServiceClient).getUser(USER_ID);
        verify(postRepository).getByIdOrThrow(POST_ID);
    }

    @Test
    void addLikeToPost_UserNotFound_ShouldThrowException() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(new RuntimeException());

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(POST_ID, USER_ID));

        verify(likeRepository, never()).save(any(Like.class));
        verify(postRepository, never()).getByIdOrThrow(anyLong());
    }

    @Test
    void addLikeToPost_PostNotFound_ShouldThrowException() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(postRepository.getByIdOrThrow(POST_ID)).thenThrow(new EntityNotFoundException("Post not found"));

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(POST_ID, USER_ID));

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void addLikeToPost_AlreadyLiked_ShouldThrowDuplicateLikeException() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(postRepository.getByIdOrThrow(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));

        assertThrows(DuplicateLikeException.class, () -> likeService.addLikeToPost(POST_ID, USER_ID));

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void removeLikeFromPost_ValidData_ShouldRemoveLike() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(likeRepository.findByPostIdAndUserIdOrThrow(POST_ID, USER_ID)).thenReturn(like);

        likeService.removeLikeFromPost(POST_ID, USER_ID);

        verify(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    void removeLikeFromPost_NotAuthor_ShouldThrowForbiddenException() {
        Like otherUserLike = Like.builder()
                .id(LIKE_ID)
                .userId(OTHER_USER_ID)
                .post(post)
                .build();

        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(likeRepository.findByPostIdAndUserIdOrThrow(POST_ID, USER_ID)).thenReturn(otherUserLike);

        assertThrows(ForbiddenException.class, () -> likeService.removeLikeFromPost(POST_ID, USER_ID));

        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void addLikeToComment_ValidData_ShouldAddLike() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(commentRepository.getByIdOrThrow(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        likeService.addLikeToComment(COMMENT_ID, USER_ID);

        verify(likeRepository).save(any(Like.class));
        verify(userServiceClient).getUser(USER_ID);
        verify(commentRepository).getByIdOrThrow(COMMENT_ID);
    }

    @Test
    void addLikeToComment_AlreadyLiked_ShouldThrowDuplicateLikeException() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(commentRepository.getByIdOrThrow(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(like));

        assertThrows(DuplicateLikeException.class, () -> likeService.addLikeToComment(COMMENT_ID, USER_ID));

        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void removeLikeFromComment_ValidData_ShouldRemoveLike() {
        Like commentLike = Like.builder()
                .id(LIKE_ID)
                .userId(USER_ID)
                .comment(comment)
                .build();

        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(likeRepository.findByCommentIdAndUserIdOrThrow(COMMENT_ID, USER_ID)).thenReturn(commentLike);

        likeService.removeLikeFromComment(COMMENT_ID, USER_ID);

        verify(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    void getCountLikeForPost_ValidData_ShouldReturnCount() {
        Integer expectedCount = 5;

        when(postRepository.getByIdOrThrow(POST_ID)).thenReturn(post);
        when(likeRepository.countLikeByPost(POST_ID)).thenReturn(expectedCount);

        Integer result = likeService.getCountLikeForPost(POST_ID);

        assertEquals(expectedCount, result);
        verify(postRepository).getByIdOrThrow(POST_ID);
        verify(likeRepository).countLikeByPost(POST_ID);
    }

    @Test
    void getCountLikeForComment_ValidData_ShouldReturnCount() {
        Integer expectedCount = 3;

        when(commentRepository.getByIdOrThrow(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.countLikeByComment(COMMENT_ID)).thenReturn(expectedCount);

        Integer result = likeService.getCountLikeForComment(COMMENT_ID);

        assertEquals(expectedCount, result);
        verify(commentRepository).getByIdOrThrow(COMMENT_ID);
        verify(likeRepository).countLikeByComment(COMMENT_ID);
    }

    @Test
    void getCountLikeUserForPosts_ValidData_ShouldReturnCount() {
        Integer expectedCount = 10;

        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(likeRepository.countLikeUserForPosts(USER_ID)).thenReturn(expectedCount);

        Integer result = likeService.getCountLikeUserForPosts(USER_ID);

        assertEquals(expectedCount, result);
        verify(userServiceClient).getUser(USER_ID);
        verify(likeRepository).countLikeUserForPosts(USER_ID);
    }

    @Test
    void getCountLikeUserForComments_ValidData_ShouldReturnCount() {
        Integer expectedCount = 7;

        when(userServiceClient.getUser(USER_ID)).thenReturn(null);
        when(likeRepository.countLikeUserForComments(USER_ID)).thenReturn(expectedCount);

        Integer result = likeService.getCountLikeUserForComments(USER_ID);

        assertEquals(expectedCount, result);
        verify(userServiceClient).getUser(USER_ID);
        verify(likeRepository).countLikeUserForComments(USER_ID);
    }

    @Test
    void getCountLikeForPost_PostNotFound_ShouldThrowException() {
        when(postRepository.getByIdOrThrow(POST_ID)).thenThrow(new EntityNotFoundException("Post not found"));

        assertThrows(EntityNotFoundException.class, () -> likeService.getCountLikeForPost(POST_ID));

        verify(likeRepository, never()).countLikeByPost(anyLong());
    }

    @Test
    void getCountLikeForComment_CommentNotFound_ShouldThrowException() {
        when(commentRepository.getByIdOrThrow(COMMENT_ID)).thenThrow(new EntityNotFoundException("Comment not found"));

        assertThrows(EntityNotFoundException.class, () -> likeService.getCountLikeForComment(COMMENT_ID));

        verify(likeRepository, never()).countLikeByComment(anyLong());
    }
}
