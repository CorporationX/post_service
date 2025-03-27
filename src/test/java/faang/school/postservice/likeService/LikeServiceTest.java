package faang.school.postservice.likeService;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.LikeException;
import faang.school.postservice.like.LikeDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    private LikeMapperImpl likeMapper;

    @InjectMocks
    private LikeService likeService;

    private final long USER_ID = 1L;
    private final long POST_ID = 2L;
    private final long COMMENT_ID = 3L;
    private Post post;
    private Comment comment;
    private Like like;
    private LikeDto likeDto;

    @BeforeEach
    public void SetUp() {
        post = Post.builder().id(POST_ID).build();
        comment = Comment.builder().id(COMMENT_ID).build();

        like = Like.builder().id(1L).userId(USER_ID).post(post).build();
        Like commentLike = Like.builder().id(2L).userId(USER_ID).comment(comment).build();

        likeDto = LikeDto.builder().id(1L).userId(USER_ID).postId(POST_ID).build();
        LikeDto commentLikeDto = LikeDto.builder().id(2L).userId(USER_ID).commentId(COMMENT_ID).build();
    }

    @Test
    public void testCreatedLikeForPost() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        LikeDto result = likeService.likePost(POST_ID, USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(POST_ID, result.getPostId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testDeletedLikeForPost() {
        doNothing().when(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);

        assertDoesNotThrow(() -> likeService.unlikePost(POST_ID, USER_ID));
        verify(likeRepository).deleteByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    public void testCreatedLikeForComment() {
        Like commentLike = Like.builder()
                .id(1L)
                .userId(USER_ID)
                .comment(comment)
                .build();

        LikeDto expectDto = LikeDto.builder()
                .id(1L)
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(commentLike);
        when(likeMapper.toDto(commentLike)).thenReturn(expectDto);

        LikeDto result = likeService.likeComment(COMMENT_ID, USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        assertEquals(COMMENT_ID, result.getCommentId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testDeletedLikeForComment() {
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);

        assertDoesNotThrow(() -> likeService.unlikeComment(COMMENT_ID, USER_ID));
        verify(likeRepository).deleteByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    public void testPostNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        assertThrows(LikeException.class, () -> likeService.likePost(POST_ID, USER_ID));
        verify(postRepository).findById(POST_ID);
    }

    @Test
    public void testPostAlreadyLiked() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.of(like));

        assertThrows(LikeException.class, () -> likeService.likePost(POST_ID, USER_ID));
        verify(likeRepository).findByPostIdAndUserId(POST_ID, USER_ID);
    }

    @Test
    public void testCommentAlreadyLiked() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.of(like));

        assertThrows(LikeException.class, () -> likeService.likeComment(COMMENT_ID, USER_ID));
        verify(likeRepository).findByCommentIdAndUserId(COMMENT_ID, USER_ID);
    }

    @Test
    public void testUserNotFound() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(FeignException.NotFound.class);

        assertThrows(LikeException.class, () -> likeService.likePost(POST_ID, USER_ID));
        verify(userServiceClient).getUser(USER_ID);
    }

    @Test
    public void testBothLike() {
        assertThrows(LikeException.class, () -> likeService.buildLike(USER_ID, post, comment));
    }
}
