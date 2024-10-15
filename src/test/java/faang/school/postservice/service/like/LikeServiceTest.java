package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisherImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    private final static long USER_ID = 5;
    private final static long POST_ID = 1;
    private final static long COMMENT_ID = 1;
    private final static long NON_EXISTING_USER_ID = 10;
    private final static long POST_AUTHOR_ID = 4;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserContext userContext;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeEventPublisherImpl likeEventPublisher;

    @Spy
    private LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    @InjectMocks
    private LikeServiceImpl likeService;

    private Post post;

    private Comment comment;

    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .id(POST_ID)
                .authorId(POST_AUTHOR_ID)
                .build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .build();
        userDto = new UserDto(USER_ID, "username", "mail@example");
    }

    @Test
    @DisplayName("Проверка лайка поста")
    public void testSuccessLikePost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
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
        verify(postRepository, times(2)).findById(POST_ID);
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка несуществующего поста")
    public void testLikeNonExistingPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertThrows(EntityNotFoundException.class, () -> likeService.likePost(POST_ID));

        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(postRepository).findById(POST_ID);
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка поста, который уже лайкнут пользователем")
    public void testLikeAlreadyLikedPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(new Like()));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertThrows(UserAlreadyLikedException.class, () -> likeService.likePost(POST_ID));

        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка отправки лайка в redis")
    public void testLikeEventPublishSuccess() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        Like like = Like.builder()
                .post(post)
                .userId(USER_ID)
                .build();
        when(likeRepository.save(like)).thenReturn(like);
        LikeEvent likeEvent = LikeEvent.builder()
                .likeAuthorId(USER_ID)
                .postAuthorId(POST_AUTHOR_ID)
                .postId(POST_ID)
                .build();
        LikeDto result = likeService.likePost(POST_ID);

        verify(likeEventPublisher).publish(likeEvent);
    }

    @Test
    @DisplayName("Проверка удаления лайка на посте")
    public void testSuccessRemoveLikeOnPost() {
        Like like = new Like();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        likeService.removeLikeOnPost(POST_ID);

        verify(likeRepository).delete(like);
        verify(userContext).getUserId();
        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления несуществующего лайка на посте")
    public void testRemoveNonExistingLikeOnPost() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeOnPost(POST_ID));
        verify(userContext).getUserId();
        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка коммента")
    public void testSuccessLikeComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
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
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка несуществующего коммента")
    public void testLikeNonExistingComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertThrows(EntityNotFoundException.class, () -> likeService.likeComment(COMMENT_ID));

        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка комента, который уже лайкнут пользователем")
    public void testLikeAlreadyLikedComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(new Like()));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertThrows(UserAlreadyLikedException.class, () -> likeService.likeComment(POST_ID));

        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления лайка на комменте")
    public void testSuccessRemoveLikeOnComment() {
        Like like = new Like();
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(like));
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        likeService.removeLikeOnComment(COMMENT_ID);

        verify(likeRepository).delete(like);
        verify(userContext).getUserId();
        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления несуществующего лайка на комменте")
    public void testRemoveNonExistingLikeOnComment() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeOnComment(COMMENT_ID));
        verify(userContext).getUserId();
        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка поста несуществующим пользователем")
    public void testLikePostByNonExistingUser() {
        when(userContext.getUserId()).thenReturn(NON_EXISTING_USER_ID);
        when(userServiceClient.getUser(NON_EXISTING_USER_ID)).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> likeService.likePost(POST_ID));
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(NON_EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Проверка лайка коммента несуществующим пользователем")
    public void testLikeCommentByNonExistingUser() {
        when(userContext.getUserId()).thenReturn(NON_EXISTING_USER_ID);
        when(userServiceClient.getUser(NON_EXISTING_USER_ID)).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> likeService.likeComment(COMMENT_ID));
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(NON_EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления лайка у поста несуществующим пользователем")
    public void testRemoveLikeOnPostByNonExistingUser() {
        when(userContext.getUserId()).thenReturn(NON_EXISTING_USER_ID);
        when(userServiceClient.getUser(NON_EXISTING_USER_ID)).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> likeService.removeLikeOnPost(POST_ID));
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(NON_EXISTING_USER_ID);
    }

    @Test
    @DisplayName("Проверка удаления лайка у коммента несуществующим пользователем")
    public void testRemoveLikeOnCommentByNonExistingUser() {
        when(userContext.getUserId()).thenReturn(NON_EXISTING_USER_ID);
        when(userServiceClient.getUser(NON_EXISTING_USER_ID)).thenReturn(null);
        assertThrows(EntityNotFoundException.class,
                () -> likeService.removeLikeOnComment(COMMENT_ID));
        verify(userContext).getUserId();
        verify(userServiceClient).getUser(NON_EXISTING_USER_ID);
    }
}
