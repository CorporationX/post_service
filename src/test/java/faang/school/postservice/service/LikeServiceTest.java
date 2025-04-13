package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserAlreadyLikedException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.like.DeleteLikeEventPublisher;
import faang.school.postservice.publisher.like.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    private static final Long POST_ID = 1L;
    private static final Long USER_ID = 2L;
    private static final Long COMMENT_ID = 3L;

    private Post post;
    private Comment comment;
    private UserDto userDto;

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostValidator postValidator;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private UserValidator userValidator;
    @Mock
    private LikeRepository likeRepository;
    @Spy
    private LikeMapper likeMapper;
    @Spy
    private PostMapper postMapper;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private DeleteLikeEventPublisher deleteLikeEventPublisher;
    @Mock
    private PostService postService;
    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .likes(List.of(new Like()))
                .build();
        comment = Comment.builder()
                .likes(List.of(new Like()))
                .build();
        userDto = new UserDto(USER_ID, "Join", "join@mail.ru");
    }

    @Test
    public void testGetAllUsersWhoLikedPostSuccessful() {
        when(postValidator.getPostById(anyLong())).thenReturn(post);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(List.of(userDto));

        List<UserDto> resultDto = likeService.getAllUsersWhoLikedPost(POST_ID);

        verify(postValidator, times(1)).getPostById(POST_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(resultDto.get(0).id(), userDto.id());
        assertEquals(resultDto.get(0).username(), userDto.username());
        assertEquals(resultDto.get(0).email(), userDto.email());
    }

    @Test
    public void testGetAllUsersWhoLikedPostEmptyList() {
        when(postValidator.getPostById(anyLong())).thenReturn(post);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.getAllUsersWhoLikedPost(POST_ID);

        verify(postValidator, times(1)).getPostById(POST_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllUsersPostNotFound() {
        validatePostNotFound();
    }

    @Test
    public void testGetAllUsersWhoLikedCommentSuccessful() {
        when(commentValidator.getCommentById(anyLong())).thenReturn(comment);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(List.of(userDto));

        List<UserDto> resultDto = likeService.getAllUsersWhoLikedComment(COMMENT_ID);

        verify(commentValidator, times(1)).getCommentById(COMMENT_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(userDto.id(), resultDto.get(0).id());
        assertEquals(userDto.username(), resultDto.get(0).username());
        assertEquals(userDto.email(), resultDto.get(0).email());
    }

    @Test
    public void testGetAllUsersWhoLikedCommentEmptyList() {
        when(commentValidator.getCommentById(anyLong())).thenReturn(comment);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.getAllUsersWhoLikedComment(COMMENT_ID);

        verify(commentValidator, times(1)).getCommentById(COMMENT_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        assertEquals(0, result.size());
    }

    @Test
    public void testGetAllUsersCommentNotFound() {
        validateCommentNotFound();
    }

    private void validatePostNotFound() {
        String errorMessage = "Post not found";
        when(postValidator.getPostById(POST_ID))
                .thenThrow(new EntityNotFoundException(errorMessage));

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postValidator.getPostById(POST_ID));

        assertEquals(errorMessage, exception.getMessage());
        verify(userServiceClient, never()).getUsersByIds(anyList());
    }

    private void validateCommentNotFound() {
        String errorMessage = "Comment not found";
        when(commentValidator.getCommentById(COMMENT_ID))
                .thenThrow(new EntityNotFoundException(errorMessage));

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> commentValidator.getCommentById(COMMENT_ID));

        assertEquals(errorMessage, exception.getMessage());
        verify(userServiceClient, never()).getUsersByIds(anyList());
    }

    @Test
    public void testLikePostSuccessful() {
        Long userId = 1L;
        Post post = Post.builder().id(POST_ID).build();
        Like like = Like.builder().userId(userId).post(post).build();
        LikeDto likeDto = new LikeDto(userId, POST_ID, null, LocalDateTime.now());

        when(userContext.getUserId()).thenReturn(userId);
        when(postValidator.getPostById(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        doNothing().when(likeEventPublisher).publish(any(LikeEvent.class));
        when(postService.getPost(POST_ID)).thenReturn(post);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);

        LikeDto result = likeService.likePost(POST_ID);

        verify(userContext, times(1)).getUserId();
        verify(postValidator, times(1)).getPostById(POST_ID);
        verify(likeRepository, times(1)).findByPostIdAndUserId(POST_ID, userId);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(likeEventPublisher, times(1)).publish(any(LikeEvent.class));
        verify(postService, times(1)).getPost(POST_ID);
        verify(likeMapper, times(1)).toLikeDto(like);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(POST_ID, result.postId());
        assertNull(result.commentId());
    }

    @Test
    public void testLikePostUserAlreadyLiked() {
        long userId = 1L;
        Like like = Like.builder().userId(userId).post(Post.builder().id(POST_ID).build()).build();

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByPostIdAndUserId(POST_ID, userId)).thenReturn(Optional.of(like));

        assertThrows(UserAlreadyLikedException.class, () -> likeService.likePost(POST_ID));

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByPostIdAndUserId(POST_ID, userId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    public void testRemoveLikeOnPostSuccessful() {
        Long userId = 1L;
        Like like = Like.builder().userId(userId).post(Post.builder().id(POST_ID).build()).build();
        LikeDto likeDto = new LikeDto(userId, POST_ID, null, LocalDateTime.now());

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByPostIdAndUserId(POST_ID, userId)).thenReturn(Optional.of(like));
        doNothing().when(deleteLikeEventPublisher).publish(any(LikeEvent.class));
        when(postService.getPost(POST_ID)).thenReturn(post);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);

        LikeDto result = likeService.removeLikeOnPost(POST_ID);

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByPostIdAndUserId(POST_ID, userId);
        verify(likeRepository, times(1)).delete(like);
        verify(deleteLikeEventPublisher, times(1)).publish(any(LikeEvent.class));
        verify(postService, times(1)).getPost(POST_ID);
        verify(likeMapper, times(1)).toLikeDto(like);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(POST_ID, result.postId());
        assertNull(result.commentId());
    }

    @Test
    public void testRemoveLikeOnPostNotFound() {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByPostIdAndUserId(POST_ID, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeOnPost(POST_ID));

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByPostIdAndUserId(POST_ID, userId);
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    public void testLikeCommentSuccessful() {
        Long userId = 1L;
        Comment comment = Comment.builder().id(COMMENT_ID).build();
        Like like = Like.builder().userId(userId).comment(comment).build();
        LikeDto likeDto = new LikeDto(userId, null, COMMENT_ID, LocalDateTime.now());

        when(userContext.getUserId()).thenReturn(userId);
        when(commentValidator.getCommentById(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);

        LikeDto result = likeService.likeComment(COMMENT_ID);

        verify(userContext, times(1)).getUserId();
        verify(commentValidator, times(1)).getCommentById(COMMENT_ID);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(COMMENT_ID, userId);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(likeMapper, times(1)).toLikeDto(like);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(COMMENT_ID, result.commentId());
        assertNull(result.postId());
    }

    @Test
    public void testLikeCommentUserAlreadyLiked() {
        long userId = 1L;
        Like like = Like.builder().userId(userId).comment(Comment.builder().id(COMMENT_ID).build()).build();

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userId)).thenReturn(Optional.of(like));

        assertThrows(UserAlreadyLikedException.class, () -> likeService.likeComment(COMMENT_ID));

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByCommentIdAndUserId(COMMENT_ID, userId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    public void testRemoveLikeOnCommentSuccessful() {
        Long userId = 1L;
        Like like = Like.builder().userId(userId).comment(Comment.builder().id(COMMENT_ID).build()).build();
        LikeDto likeDto = new LikeDto(userId, null, COMMENT_ID, LocalDateTime.now());

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userId)).thenReturn(Optional.of(like));
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);

        LikeDto result = likeService.removeLikeOnComment(COMMENT_ID);

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByCommentIdAndUserId(COMMENT_ID, userId);
        verify(likeRepository, times(1)).delete(like);
        verify(likeMapper, times(1)).toLikeDto(like);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(COMMENT_ID, result.commentId());
        assertNull(result.postId());
    }

    @Test
    public void testRemoveLikeOnCommentNotFound() {
        long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeOnComment(COMMENT_ID));

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByCommentIdAndUserId(COMMENT_ID, userId);
        verify(likeRepository, never()).delete(any(Like.class));
    }

    @Test
    public void testCountLikesPostSuccessful() {
        Post post = Post.builder().id(POST_ID).likes(List.of(new Like(), new Like())).build();
        PostDto postDto = new PostDto(POST_ID, 1L, 1L, 2L);

        when(postValidator.getPostById(POST_ID)).thenReturn(post);
        when(postMapper.toDto(post)).thenReturn(postDto);

        PostDto result = likeService.countLikesPost(POST_ID);

        verify(postValidator, times(1)).getPostById(POST_ID);
        verify(postMapper, times(1)).toDto(post);

        assertNotNull(result);
        assertEquals(POST_ID, result.getId());
        assertEquals(2L, result.getLikes());
    }

    @Test
    public void testValidateAndGetUserIdSuccessful() {
        Long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(userValidator).validateUserExist(userId);

        Long result = likeService.validateAndGetUserId();

        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExist(userId);

        assertEquals(userId, result);
    }

    @Test
    public void testValidateAndGetUserIdUserNotFound() {
        Long userId = 1L;

        when(userContext.getUserId()).thenReturn(userId);
        doThrow(new EntityNotFoundException("User not found")).when(userValidator).validateUserExist(userId);

        assertThrows(EntityNotFoundException.class, () -> likeService.validateAndGetUserId());

        verify(userContext, times(1)).getUserId();
        verify(userValidator, times(1)).validateUserExist(userId);
    }
}
