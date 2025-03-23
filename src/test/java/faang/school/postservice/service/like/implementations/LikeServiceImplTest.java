package faang.school.postservice.service.like.implementations;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.LikeAlreadyExistException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Spy
    private LikeMapper likeMapper = Mappers.getMapper(LikeMapper.class);

    private LikeDto postLikeDto;
    private LikeDto commentLikeDto;
    private Like postLike;
    private Like commentLike;
    private Post post;
    private Comment comment;
    private long postId;
    private long commentId;
    private long userId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        postId = 2L;
        commentId = 3L;
        post = Post.builder().id(postId).build();
        postLike = Like.builder().userId(userId).post(post).build();
        comment = Comment.builder().id(commentId).build();
        commentLike = Like.builder().userId(userId).comment(comment).build();
    }

    @Test
    void testLikePostWhenLikeCreated() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(postLike)).thenReturn(postLike);

        LikeDto result = likeService.likePost(postId, userId);

        assertNotNull(result);
        assertEquals(postId, result.getPostId());
        assertEquals(userId, result.getUserId());
        verify(userServiceClient).getUser(userId);
        verify(likeRepository).save(postLike);
        verify(likeMapper).toDto(postLike);
    }

    @Test
    void testLikePostWhenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> likeService.likePost(postId, userId),
                "Post not found: postId=" + postId);
    }

    @Test
    void testLikePostWhenAuthorNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        doThrow(AuthorNotFoundException.class).when(userServiceClient).getUser(userId);

        assertThrows(AuthorNotFoundException.class, () -> likeService.likePost(postId, userId),
                "Author with id " + userId + " not found");
    }

    @Test
    void testLikePostWhenLikeAlreadyExists() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(postLike));

        assertThrows(LikeAlreadyExistException.class, () -> likeService.likePost(postId, userId),
                String.format("Like already exist: postId=%d, userId=%d", postId, userId));
    }

    @Test
    void testUnlikePostWhenUnliked() {
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(postLike));

        likeService.unlikePost(postId, userId);

        verify(likeRepository).delete(postLike);
    }

    @Test
    void testUnlikePostWhenLikeNotFound() {
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.unlikePost(postId, userId),
                String.format("Like not found: postId=%d, userId=%d", postId, userId));
    }

    @Test
    void testLikeCommentWhenLikeCreated() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());
        when(likeRepository.save(commentLike)).thenReturn(commentLike);

        LikeDto result = likeService.likeComment(commentId, userId);

        assertNotNull(result);
        assertEquals(commentId, result.getCommentId());
        assertEquals(userId, result.getUserId());
        verify(userServiceClient).getUser(userId);
        verify(likeRepository).save(commentLike);
        verify(likeMapper).toDto(commentLike);
    }

    @Test
    void testLikeCommentWhenCommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () -> likeService.likeComment(commentId, userId),
                "Comment not found: commentId=" + commentId);
    }

    @Test
    void testLikeCommentWhenAuthorNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doThrow(AuthorNotFoundException.class).when(userServiceClient).getUser(userId);

        assertThrows(AuthorNotFoundException.class, () -> likeService.likeComment(commentId, userId),
                "Author with id " + userId + " not found");
    }

    @Test
    void testLikeCommentWhenLikeAlreadyExists() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(commentLike));

        assertThrows(LikeAlreadyExistException.class, () -> likeService.likeComment(commentId, userId),
                String.format("Like already exist: commentId=%d, userId=%d", commentId, userId));
    }

    @Test
    void testUnlikeCommentWhenUnliked() {
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(commentLike));

        likeService.unlikeComment(commentId, userId);

        verify(likeRepository).delete(commentLike);
    }

    @Test
    void testUnlikeCommentWhenLikeNotFound() {
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(LikeNotFoundException.class, () -> likeService.unlikeComment(commentId, userId),
                String.format("Like not found: commentId=%d, userId=%d", commentId, userId));
    }
}