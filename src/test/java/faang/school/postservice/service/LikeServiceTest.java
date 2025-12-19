package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.LikeServiceImpl;
import faang.school.postservice.validator.like.LikeValidator;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    private final static long DEFAULT_USER_ID = 1L;
    private final static long DEFAULT_ID = 1L;
    private final static long DEFAULT_PROJECT_ID = 4L;
    private final static boolean DEFAULT_TRUE_SIGN = true;
    private final static boolean DEFAULT_FALSE_SIGN = false;
    private final static long NOT_EXIST_ID = 123L;
    private final static int LIKES_COUNT = 2;

    @InjectMocks
    private LikeServiceImpl likeService;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidator likeValidator;
    @Spy
    private LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);
    @Captor
    ArgumentCaptor<Like> likeCaptor;

    private final long userId = DEFAULT_USER_ID;
    private final long postId = DEFAULT_ID;
    private final long commentId = DEFAULT_ID;
    private final long authorId = DEFAULT_USER_ID;
    private final long projectId = DEFAULT_PROJECT_ID;
    private final boolean isPublished = DEFAULT_TRUE_SIGN;
    private final boolean isDeleted = DEFAULT_FALSE_SIGN;
    private final long notExistPostId = NOT_EXIST_ID;
    private final long notExistCommentId = NOT_EXIST_ID;
    private final long notExistUserId = NOT_EXIST_ID;
    private final boolean isTrue = DEFAULT_TRUE_SIGN;
    private final boolean isFalse = DEFAULT_FALSE_SIGN;
    private final int likeCount = LIKES_COUNT;

    Request request = Request.create(
            Request.HttpMethod.GET,
            "http://localhost:8080/api/users/" + notExistUserId,
            Collections.emptyMap(),
            null,
            new RequestTemplate()
    );

    FeignException.InternalServerError internalServerError =
            new FeignException.InternalServerError(
                    "Internal Server Error",
                    request,
                    "User Not Found".getBytes(StandardCharsets.UTF_8),
                    Collections.emptyMap()
            );

    private final UserDto userDto = UserDto.builder()
            .id(userId)
            .username("Default User")
            .email("DefaultUser@gmail.com")
            .build();

    Post post = Post.builder()
            .id(postId)
            .likes(List.of(new Like(), new Like()))
            .authorId(authorId)
            .content("Post #1")
            .comments(new ArrayList<>())
            .projectId(projectId)
            .published(isPublished)
            .deleted(isDeleted)
            .build();

    Comment comment = Comment.builder()
            .id(commentId)
            .authorId(authorId)
            .content("Comment #1")
            .likes(new ArrayList<>())
            .post(post)
            .build();

    LikeDto likeDtoForPost = LikeDto.builder()
            .userId(userId)
            .postId(postId)
            .build();

    LikeDto likeDtoForComment = LikeDto.builder()
            .userId(userId)
            .commentId(commentId)
            .build();

    LikeDto likeDto;
    Like savedLike;

    @Test
    void testSuccessfullyLikeOnPostSet() {
        when(userContext.getUserId()).thenReturn(userId);
        when(likeValidator.validateLikeOnPost(postId, userId, isTrue)).thenReturn(post);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));

        likeDto = likeService.setLikeOnPost(postId);

        assertEquals(likeDtoForPost, likeDto);
        verify(likeRepository, times(1)).save(likeCaptor.capture());
        verify(likeMapper, times(1)).toLikeDto(any(Like.class));
        savedLike = likeCaptor.getValue();
        assertEquals(likeDtoForPost.userId(), savedLike.getUserId());
        assertEquals(likeDtoForPost.postId(), savedLike.getPost().getId());
        assertNull(savedLike.getComment());
    }

    @Test
    void testSuccessfullyLikeOnCommentSet() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        when(likeValidator.validateLikeOnComment(commentId, userId, isTrue)).thenReturn(comment);

        likeDto = likeService.setLikeOnComment(commentId);

        assertEquals(likeDtoForComment, likeDto);
        verify(likeRepository, times(1)).save(likeCaptor.capture());
        verify(likeMapper, times(1)).toLikeDto(any(Like.class));
        savedLike = likeCaptor.getValue();
        assertEquals(likeDtoForComment.userId(), savedLike.getUserId());
        assertEquals(likeDtoForComment.commentId(), savedLike.getComment().getId());
        assertNull(savedLike.getPost());
    }

    @Test
    void testFailSetLikeWhenPostDoesNotExist() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new IllegalArgumentException("Post doesn't exist"))
                .when(likeValidator)
                .validateLikeOnPost(notExistPostId, userId, isTrue);

        assertThrows(IllegalArgumentException.class,
                () -> likeService.setLikeOnPost(notExistPostId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testFailWhenLikeAlreadySetOnPost() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new DataValidationException("User has already set like for the post"))
                .when(likeValidator)
                .validateLikeOnPost(postId, userId, isTrue);

        assertThrows(DataValidationException.class,
                () -> likeService.setLikeOnPost(postId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testFailSetLikeWhenCommentDoesNotExist() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new IllegalArgumentException("Comment doesn't exist"))
                .when(likeValidator)
                .validateLikeOnComment(notExistCommentId, userId, isTrue);

        assertThrows(IllegalArgumentException.class,
                () -> likeService.setLikeOnComment(notExistCommentId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testFailWhenLikeAlreadySetOnComment() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new DataValidationException("User has already set like for the comment"))
                .when(likeValidator)
                .validateLikeOnComment(commentId, userId, isTrue);

        assertThrows(DataValidationException.class,
                () -> likeService.setLikeOnComment(commentId));
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testSuccessfullyLikeOnPostUnset() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        when(likeValidator.validateLikeOnPost(postId, userId, isFalse)).thenReturn(post);

        likeService.unsetLikeOnPost(postId);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void testSuccessfullyLikeOnCommentUnset() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        when(likeValidator.validateLikeOnComment(commentId, userId, isFalse)).thenReturn(comment);

        likeService.unsetLikeOnComment(commentId);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testFailUnsetLikeWhenPostDoesNotExist() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new IllegalArgumentException("Post doesn't exist"))
                .when(likeValidator)
                .validateLikeOnPost(notExistPostId, userId, isFalse);

        assertThrows(IllegalArgumentException.class,
                () -> likeService.unsetLikeOnPost(notExistPostId));
        verify(likeRepository, never()).deleteByPostIdAndUserId(notExistPostId, userId);
    }

    @Test
    void testFailUnsetLikeWhenLikeNotSetOnPost() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new DataValidationException("User hasn't set like for the post"))
                .when(likeValidator)
                .validateLikeOnPost(postId, userId, isFalse);

        assertThrows(DataValidationException.class,
                ()-> likeService.unsetLikeOnPost(postId));
        verify(likeRepository, never()).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void testFailUnsetLikeWhenCommentDoesNotExist() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new IllegalArgumentException("Comment doesn't exist"))
                .when(likeValidator)
                .validateLikeOnComment(notExistCommentId, userId, isFalse);

        assertThrows(IllegalArgumentException.class,
                () -> likeService.unsetLikeOnComment(notExistCommentId));
        verify(likeRepository, never()).deleteByCommentIdAndUserId(notExistCommentId, userId);
    }

    @Test
    void testFailUnsetLikeWhenLikeNotSetOnComment() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(ResponseEntity.ofNullable(userDto));
        doThrow(new DataValidationException("User hasn't set like for the comment"))
                .when(likeValidator)
                .validateLikeOnComment(commentId, userId, isFalse);

        assertThrows(DataValidationException.class,
                ()-> likeService.unsetLikeOnComment(commentId));
        verify(likeRepository, never()).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testFailSetLikeOnPostWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(notExistUserId);
        doThrow(internalServerError)
                .when(userServiceClient)
                .getUser(notExistUserId);
        assertThrows(FeignException.InternalServerError.class,
                () -> likeService.setLikeOnPost(postId));
        verify(userServiceClient, times(1)).getUser(notExistUserId);
        verify(likeRepository, never()).save(any(Like.class));

    }

    @Test
    void testFailSetLikeOnCommentWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(notExistUserId);
        doThrow(internalServerError)
                .when(userServiceClient)
                .getUser(notExistUserId);
        assertThrows(FeignException.InternalServerError.class,
                () -> likeService.setLikeOnComment(commentId));
        verify(userServiceClient, times(1)).getUser(notExistUserId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testFailUnsetLikeOnPostWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(notExistUserId);
        doThrow(internalServerError)
                .when(userServiceClient)
                .getUser(notExistUserId);
        assertThrows(FeignException.InternalServerError.class,
                () -> likeService.unsetLikeOnPost(postId));
        verify(userServiceClient, times(1)).getUser(notExistUserId);
        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());

    }

    @Test
    void testFailUnsetLikeOnCommentWhenUserNotFound() {
        when(userContext.getUserId()).thenReturn(notExistUserId);
        doThrow(internalServerError)
                .when(userServiceClient)
                .getUser(notExistUserId);
        assertThrows(FeignException.InternalServerError.class,
                () -> likeService.unsetLikeOnComment(commentId));
        verify(userServiceClient, times(1)).getUser(notExistUserId);
        verify(likeRepository, never()).deleteByCommentIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void testSuccessfullyPostLikesCountGet() {
        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(post));
        int resultCount = likeService.getPostLikesCount(postId);
        assertEquals(likeCount, resultCount);
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testPostLikesCountGetWhenPostNotExist() {
        when(postRepository.findById(notExistPostId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> likeService.getPostLikesCount(notExistPostId));
    }
}
