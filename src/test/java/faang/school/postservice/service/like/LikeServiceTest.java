package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.ike.CommentLikeDto;
import faang.school.postservice.dto.ike.PostLikeDto;
import faang.school.postservice.exception.LikeException;
import faang.school.postservice.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.service.feed.FeedRedisService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static faang.school.postservice.service.LikeService.ALREADY_LIKED;
import static faang.school.postservice.service.LikeService.ERROR_VALIDATING_USER;
import static faang.school.postservice.service.LikeService.LIKE_NOT_FOUND;
import static faang.school.postservice.service.LikeService.POST_NOT_FOUND;
import static faang.school.postservice.service.LikeService.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private LikeMapper likeMapper;

    @InjectMocks
    private LikeService likeService;

    @Mock
    private FeedRedisService feedRedisService;

    private final long userId = 1L;
    private final long postId = 2L;
    private final long commentId = 3L;

    private Post post;
    private Comment comment;
    private Like like;
    private LikeDto likeDto;
    private final PostLikeDto postLikeDto = new PostLikeDto(postId, userId);
    private final CommentLikeDto commentLikeDto = new CommentLikeDto(commentId, userId);

    @BeforeEach
    public void setUp() {
        post = Post.builder().id(postId).build();
        comment = Comment.builder().id(commentId).build();

        like = Like.builder().id(1L).userId(userId).post(post).comment(null).build();

        likeDto = LikeDto.builder().id(1L).userId(userId).postId(postId).commentId(null).build();
    }

    //Positive tests
    @Test
    public void testCreatedLikeForPost() {

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        likeService.likePostConsumer(postLikeDto);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(feedRedisService, times(1)).incrementPostLikes(postId);
    }

    @Test
    public void testDeletedLikeForPost() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);
        doNothing().when(likeRepository).deleteByPostIdAndUserId(postId, userId);

        assertDoesNotThrow(() -> likeService.unlikePostConsumer(postLikeDto));
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
        verify(feedRedisService, times(1)).decrementPostLikes(postId);
    }

    @Test
    public void testCreatedLikeForComment() {
        Like commentLike = Like.builder().id(2L).userId(userId).comment(comment).build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(commentLike);

        likeService.likeCommentConsumer(commentLikeDto);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(feedRedisService, times(1)).incrementCommentLikes(commentId);
    }

    @Test
    public void testDeletedLikeForComment() {
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(commentId, userId);

        assertDoesNotThrow(() -> likeService.unlikeCommentConsumer(commentLikeDto));
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
        verify(feedRedisService, times(1)).decrementCommentLikes(commentId);
    }

    //Negative Tests
    @Test
    public void testPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        LikeException exception = assertThrows(LikeException.class,
                () -> likeService.likePostConsumer(postLikeDto)
        );

        assertEquals(POST_NOT_FOUND.formatted(postLikeDto.getPostId()), exception.getMessage());
        verify(postRepository).findById(postId);
    }

    @Test
    public void testPostAlreadyLiked() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        LikeException exception = assertThrows(LikeException.class,
                () -> likeService.likePostConsumer(postLikeDto)
        );

        assertEquals(ALREADY_LIKED, exception.getMessage());
        verify(likeRepository).findByPostIdAndUserId(postId, userId);
    }

    @Test
    public void testCommentAlreadyLiked() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(like));

        LikeException exception = assertThrows(LikeException.class,
                () -> likeService.likeCommentConsumer(commentLikeDto)
        );

        assertEquals(ALREADY_LIKED, exception.getMessage());
        verify(likeRepository).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    public void testUserNotFound() {
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        LikeException exception = assertThrows(LikeException.class,
                () -> likeService.likePostConsumer(postLikeDto)
        );

        assertEquals(USER_NOT_FOUND.formatted(postLikeDto.getUserId()), exception.getMessage());
        verify(userServiceClient).getUser(userId);
    }

    @Test
    public void testUserValidationError() {
        when(userServiceClient.getUser(userId)).thenThrow(FeignException.InternalServerError.class);

        LikeException exception = assertThrows(LikeException.class,
                () -> likeService.likePostConsumer(postLikeDto)
        );

        assertEquals(ERROR_VALIDATING_USER.formatted(postLikeDto.getUserId()), exception.getMessage());
        verify(userServiceClient).getUser(userId);
    }

    @Test
    public void testBothLike() {
        assertThrows(LikeException.class, () -> likeService.buildLike(userId, post, comment));
    }

    @Test
    public void testLikeWithArgumentCaptor() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        ArgumentCaptor<Like> likeArgumentCaptor = ArgumentCaptor.forClass(Like.class);
        likeService.likePostConsumer(postLikeDto);

        verify(likeRepository, times(1)).save(likeArgumentCaptor.capture());
        verify(feedRedisService, times(1)).incrementPostLikes(postId);
        Like capturedLike = likeArgumentCaptor.getValue();
        assertEquals(userId, capturedLike.getUserId());
        assertEquals(postId, capturedLike.getPost().getId());
    }

    @Test
    public void testLikeNotFound() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);

        LikeException exception = assertThrows(LikeException.class,
                () -> likeService.unlikePostConsumer(postLikeDto)
        );

        assertEquals(LIKE_NOT_FOUND.formatted(postId, userId), exception.getMessage());
        verify(likeRepository, never()).deleteByPostIdAndUserId(postId, userId);
    }
}
