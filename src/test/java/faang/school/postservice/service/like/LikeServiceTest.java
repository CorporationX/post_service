package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.authorization.UserUnauthorizedException;
import faang.school.postservice.exception.client.RemoteNotFoundException;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.like.LikeAlreadyExistsException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.post.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.like.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final long USER_ID = 2L;
    private static final long LIKE_ID = 3L;
    private static final long POST_ID = 2L;
    private static final long COMMENT_ID = 4L;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private LikeValidator likeValidator;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private LikeService likeService;

    private Like like;
    private Post post;
    private Comment comment;

    @BeforeEach
    public void setUp() {
        like = new Like();
        like.setId(LIKE_ID);
        post = new Post();
        post.setId(POST_ID);
        comment = new Comment();
        comment.setId(COMMENT_ID);
    }

    @Test
    public void testAddLikeToPost_UserUnauthorized() {
        when(userContext.getUserId()).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> likeService.addLikeToPost(POST_ID));
        verify(likeRepository, never()).save(any());
    }

    @Test
    public void testAddLikeToPost_LikeAuthorNotFound() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        doThrow(RemoteNotFoundException.class)
                .when(likeValidator)
                .checkLikeAuthorExists(USER_ID);

        assertThrows(RemoteNotFoundException.class, () -> likeService.addLikeToPost(POST_ID));
        verify(likeRepository, never()).save(any());
    }

    @Test
    public void testAddLikeToPost_PostNotFound() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        doThrow(PostNotFoundException.class)
                .when(postService)
                .getPostById(POST_ID);

        assertThrows(PostNotFoundException.class, () -> likeService.addLikeToPost(POST_ID));
        verify(likeValidator, times(1)).checkLikeAuthorExists(USER_ID);
        verify(likeRepository, never()).save(any());
    }

    @Test
    public void testAddLikeToPost_UserHasAlreadyLikedPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postService.getPostById(POST_ID)).thenReturn(post);
        doThrow(LikeAlreadyExistsException.class)
                .when(likeValidator)
                .checkUserHasNoLikeOnPost(USER_ID, POST_ID);

        assertThrows(LikeAlreadyExistsException.class, () -> likeService.addLikeToPost(POST_ID));
        verify(likeValidator, times(1)).checkLikeAuthorExists(USER_ID);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void testAddLikeToPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(likeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Like result = likeService.addLikeToPost(POST_ID);

        assertEquals(USER_ID, result.getUserId());
        assertEquals(post, result.getPost());
        verify(likeValidator).checkLikeAuthorExists(USER_ID);
        verify(likeValidator).checkUserHasNoLikeOnPost(USER_ID, POST_ID);
        verify(likeRepository).save(any());
    }

    @Test
    public void testAddLikeToComment_UserUnauthorized() {
        when(userContext.getUserId()).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> likeService.addLikeToComment(COMMENT_ID));
        verify(likeRepository, never()).save(any());
    }

    @Test
    public void testAddLikeToComment_LikeAuthorNotFound() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        doThrow(RemoteNotFoundException.class)
                .when(likeValidator)
                .checkLikeAuthorExists(USER_ID);

        assertThrows(RemoteNotFoundException.class, () -> likeService.addLikeToComment(COMMENT_ID));
        verify(likeRepository, never()).save(any());
    }

    @Test
    public void testAddLikeToComment_CommentNotFound() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        doThrow(CommentNotFoundException.class)
                .when(commentService)
                .get(COMMENT_ID);

        assertThrows(CommentNotFoundException.class, () -> likeService.addLikeToComment(COMMENT_ID));
        verify(likeValidator, times(1)).checkLikeAuthorExists(USER_ID);
        verify(likeRepository, never()).save(any());
    }

    @Test
    public void testAddLikeToComment_UserHasAlreadyLikedComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentService.get(COMMENT_ID)).thenReturn(comment);
        doThrow(LikeAlreadyExistsException.class)
                .when(likeValidator)
                .checkUserHasNoLikeOnComment(USER_ID, COMMENT_ID);

        assertThrows(LikeAlreadyExistsException.class, () -> likeService.addLikeToComment(COMMENT_ID));
        verify(likeValidator, times(1)).checkLikeAuthorExists(USER_ID);
        verify(likeRepository, never()).save(any());
    }

    @Test
    void testAddLikeToComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(commentService.get(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Like result = likeService.addLikeToComment(COMMENT_ID);

        assertEquals(USER_ID, result.getUserId());
        assertEquals(comment, result.getComment());
        verify(likeValidator).checkLikeAuthorExists(USER_ID);
        verify(likeValidator).checkUserHasNoLikeOnComment(USER_ID, COMMENT_ID);
        verify(likeRepository).save(any());
    }

    @Test
    public void testDeleteLikeFromPost_UserUnauthorized() {
        when(userContext.getUserId()).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> likeService.deleteLikeFromPost(POST_ID));
        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());
    }

    @Test
    public void testDeleteLikeFromPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);

        likeService.deleteLikeFromPost(POST_ID);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    public void testDeleteLikeFromComment_UserUnauthorized() {
        when(userContext.getUserId()).thenThrow(UserUnauthorizedException.class);

        assertThrows(UserUnauthorizedException.class, () -> likeService.deleteLikeFromComment(COMMENT_ID));
        verify(likeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
    }

    @Test
    public void testDeleteLikeFromComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);

        likeService.deleteLikeFromComment(COMMENT_ID);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }
}