package faang.school.postservice.service.like;

import faang.school.postservice.broker.producer.PostLikeEventProducer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.mapper.like.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.like.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeServiceValidator likeServiceValidator;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostLikeEventProducer postLikeEventProducer;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Spy
    private LikeMapperImpl likeMapper;

    private final Long userId = 1L;
    private final Long postId = 2L;
    private final Long commentId = 3L;

    @BeforeEach
    void setUp() {
        when(userContext.getUserId()).thenReturn(userId);

        likeService = new LikeServiceImpl(
                userContext,
                likeRepository,
                postRepository,
                commentRepository,
                likeServiceValidator,
                userServiceClient,
                postLikeEventProducer,
                likeEventPublisher,
                likeMapper);
    }

    @Test
    void testCreateLikeForPostShouldSaveLike() {
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doNothing().when(likeServiceValidator).validatePostLiked(postId, userId);

        likeService.createLikeForPost(postId);

        verify(likeServiceValidator).validatePostLiked(postId, userId);
        verify(postRepository).findById(postId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void testCreateLikeForPostShouldThrowExceptionWhenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.createLikeForPost(postId));

        verify(postRepository).findById(postId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testCreateLikeForCommentShouldSaveLike() {
        Comment comment = new Comment();
        comment.setId(commentId);
        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(likeServiceValidator).validateCommentLiked(commentId, userId);

        likeService.createLikeForComment(commentId);

        verify(likeServiceValidator).validateCommentLiked(commentId, userId);
        verify(commentRepository).findById(commentId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void testCreateLikeForCommentShouldThrowExceptionWhenCommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.createLikeForComment(commentId));

        verify(commentRepository).findById(commentId);
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void testDeleteLikeFromPostShouldDeleteLike() {
        doNothing().when(likeRepository).deleteByPostIdAndUserId(postId, userId);

        likeService.deleteLikeFromPost(postId);
        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void testDeleteLikeFromCommentShouldDeleteLike() {
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(commentId, userId);

        likeService.deleteLikeFromComment(commentId);
        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }
}