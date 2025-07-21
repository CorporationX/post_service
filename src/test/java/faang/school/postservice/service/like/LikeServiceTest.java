package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class LikeServiceTest {
    @InjectMocks
    private LikeService likeService;
    @Mock
    private UserContext context;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Captor
    private ArgumentCaptor<Like> likeCaptor;

    private static final long POST_ID = 1;
    private static final long COMMENT_ID = 2;
    private static final long USER_ID = 3;
    private static final long LIKE_ID = 4;

    @Test
    @DisplayName("Успешный лайк поста")
    void positive_shouldAddLikeToPost() {
        when(context.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(preparePost(false));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.existsByUserIdAndCommentPostId(USER_ID, POST_ID)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenReturn(prepareLike());

        likeService.addToPost(POST_ID);

        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like actualLike = likeCaptor.getValue();
        assertEquals(POST_ID, actualLike.getPost().getId());
        assertEquals(USER_ID, actualLike.getUserId());
    }

    @Test
    @DisplayName("Успешный лайк комментария")
    void positive_shouldAddLikeToComment() {
        when(context.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareComment());
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(prepareLike());

        likeService.addToComment(COMMENT_ID);

        verify(likeRepository, times(1)).save(likeCaptor.capture());
        Like actualLike = likeCaptor.getValue();
        assertEquals(COMMENT_ID, actualLike.getComment().getId());
        assertEquals(USER_ID, actualLike.getUserId());
    }

    @Test
    @DisplayName("Успешное удаление лайка с поста")
    void positive_shouldDeleteLikeFromPost() {
        when(context.getUserId()).thenReturn(USER_ID);

        likeService.deleteFromPost(POST_ID);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    @DisplayName("Успешное удаление лайка с комментария")
    void positive_shouldDeleteLikeFromComment() {
        when(context.getUserId()).thenReturn(USER_ID);

        likeService.deleteFromComment(COMMENT_ID);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    @DisplayName("Ошибка лайка поста - не найден пост по id")
    void negative_whenPostNotFound_throwsException() {
        String expectedExceptionMessage = "Post " + POST_ID + " not found";
        when(context.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(EntityNotFoundException.class,
                                                     () -> likeService.addToPost(POST_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    @Test
    @DisplayName("Ошибка лайка поста - пользователь не найден")
    void negative_whenUserLikedPostNotFound_throwsException() {
        when(context.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(preparePost(false));
        when(userServiceClient.getUser(USER_ID)).thenThrow(FeignException.class);

        verify(likeRepository, never()).save(any(Like.class));
        assertThrows(FeignException.class,
                     () -> likeService.addToPost(POST_ID));
    }

    @Test
    @DisplayName("Ошибка лайка поста - пост удален")
    void negative_whenPostDeleted_throwsException() {
        String expectedExceptionMessage = "Post " + POST_ID + " already deleted";
        when(context.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(preparePost(true));

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(IllegalStateException.class,
                                                     () -> likeService.addToPost(POST_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);

    }

    @Test
    @DisplayName("Ошибка лайка поста - пост уже лайкнут")
    void negative_whenPostAlreadyLiked_throwsException() {
        String expectedExceptionMessage = "User " + USER_ID + " already liked post " + POST_ID;
        when(context.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(preparePost(false));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(new Like()));

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(IllegalStateException.class,
                                                     () -> likeService.addToPost(POST_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    @Test
    @DisplayName("Ошибка лайка поста - уже лайкнут комментарий к посту")
    void negative_whenCommentOfPostAlreadyLiked_throwsException() {
        String expectedExceptionMessage = "User " + USER_ID + " already liked comment for post " + POST_ID;
        when(context.getUserId()).thenReturn(USER_ID);
        when(postRepository.findById(POST_ID)).thenReturn(preparePost(false));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.existsByUserIdAndCommentPostId(USER_ID, POST_ID)).thenReturn(true);

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(IllegalStateException.class,
                                                     () -> likeService.addToPost(POST_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    @Test
    @DisplayName("Ошибка лайка комментария - не найден комментарий по id")
    void negative_whenCommentNotFound_throwsException() {
        String expectedExceptionMessage = "Comment " + COMMENT_ID + " not found";
        when(context.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(EntityNotFoundException.class,
                                                     () -> likeService.addToComment(COMMENT_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    @Test
    @DisplayName("Ошибка лайка комментария - пользователь не найден")
    void negative_whenUserLikedCommentNotFound_throwsException() {
        when(context.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareComment());
        when(userServiceClient.getUser(USER_ID)).thenThrow(FeignException.class);

        verify(likeRepository, never()).save(any(Like.class));
        assertThrows(FeignException.class,
                     () -> likeService.addToComment(COMMENT_ID));
    }

    @Test
    @DisplayName("Ошибка лайка комментария - комментарий уже лайкнут")
    void negative_whenCommentAlreadyLiked_throwsException() {
        String expectedExceptionMessage = "User " + USER_ID + " already liked comment " + COMMENT_ID;
        when(context.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareComment());
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(prepareLike()));

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(IllegalStateException.class,
                                                     () -> likeService.addToComment(COMMENT_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    @Test
    @DisplayName("Ошибка лайка поста - уже лайкнут пост комментария")
    void negative_whenPostOfCommentLiked_throwsException() {
        String expectedExceptionMessage = "User " + USER_ID + " already liked post " + POST_ID;
        when(context.getUserId()).thenReturn(USER_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(prepareComment());
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(prepareLike()));

        verify(likeRepository, never()).save(any(Like.class));
        String actualExceptionMessage = assertThrows(IllegalStateException.class,
                                                     () -> likeService.addToComment(COMMENT_ID)).getMessage();
        assertEquals(expectedExceptionMessage, actualExceptionMessage);
    }

    // ---------------------------------

    private Optional<Post> preparePost(boolean isDeleted) {
        return Optional.of(
                Post.builder()
                        .id(POST_ID)
                        .deleted(isDeleted)
                        .build());
    }

    private Optional<Comment> prepareComment() {
        return Optional.of(
                Comment.builder()
                        .id(COMMENT_ID)
                        .post(preparePost(false).get())
                        .build());
    }

    private Like prepareLike() {
        return Like.builder()
                .id(LIKE_ID)
                .build();
    }
}