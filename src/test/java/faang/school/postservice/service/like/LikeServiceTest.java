package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    private final static long USER_ID = 5;
    private final static long POST_ID = 1;
    private final static long COMMENT_ID = 1;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserContext userContext;

    @Spy
    private LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    @InjectMocks
    private LikeServiceImpl likeService;

    private Post post;

    private Comment comment;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .id(POST_ID)
                .build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .build();
    }

    @Test
    @DisplayName("Проверка лайка поста")
    public void testSuccessLikePost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        Like like = Like.builder()
                .post(post)
                .userId(USER_ID)
                .build();
        when(likeRepository.save(like)).thenReturn(like);

        LikeDto result = likeService.likePost(POST_ID);

        assertEquals(USER_ID, result.userId());
        assertEquals(POST_ID, result.postId());
        assertNull(result.commentId());
        verify(likeRepository).save(like);
        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(postRepository).findById(POST_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Проверка лайка несуществующего поста")
    public void testLikeNonExistingPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.likePost(POST_ID));

        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(postRepository).findById(POST_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Проверка лайка поста, который уже лайкнут пользователем")
    public void testLikeAlreadyLikedPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(new Like()));

        assertThrows(UserAlreadyLikedException.class, () -> likeService.likePost(POST_ID));

        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Проверка удаления лайка на посте")
    public void testSuccessRemoveLikeOnPost() {
        Like like = new Like();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));

        likeService.removeLikeOnPost(POST_ID);

        verify(likeRepository).delete(like);
        verify(userContext).getUserId();
        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления несуществующего лайка на посте")
    public void testRemoveNonExistingLikeOnPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeOnPost(POST_ID));
        verify(userContext).getUserId();
        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка коммента")
    public void testSuccessLikeComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        Like like = Like.builder()
                .comment(comment)
                .userId(USER_ID)
                .build();
        when(likeRepository.save(like)).thenReturn(like);

        LikeDto result = likeService.likeComment(COMMENT_ID);

        assertEquals(USER_ID, result.userId());
        assertEquals(COMMENT_ID, result.commentId());
        assertNull(result.postId());
        verify(likeRepository).save(like);
        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Проверка лайка несуществующего коммента")
    public void testLikeNonExistingComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.likeComment(COMMENT_ID));

        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Проверка лайка комента, который уже лайкнут пользователем")
    public void testLikeAlreadyLikedComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(new Like()));

        assertThrows(UserAlreadyLikedException.class, () -> likeService.likeComment(POST_ID));

        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Проверка удаления лайка на комменте")
    public void testSuccessRemoveLikeOnComment() {
        Like like = new Like();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(like));

        likeService.removeLikeOnComment(COMMENT_ID);

        verify(likeRepository).delete(like);
        verify(userContext).getUserId();
        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления несуществующего лайка на комменте")
    public void testRemoveNonExistingLikeOnComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeOnComment(COMMENT_ID));
        verify(userContext).getUserId();
        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }
}
