package faang.school.postservice.service;

import faang.school.postservice.dto.CommentLikeDto;
import faang.school.postservice.dto.PostLikeDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private LikeMapperImpl likeMapper;
    @InjectMocks
    private LikeServiceImpl likeServiceImpl;

    private Long userId;
    private Long postId;
    private Long commentId;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        userId = 1L;
        postId = 1L;
        commentId = 1L;

        post = new Post();
        post.setId(postId);

        comment = new Comment();
        comment.setId(commentId);
    }

    @Test
    void testLikePost() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like like = invocation.getArgument(0);
            like.setPost(post);
            return like;
        });

        PostLikeDto dto = likeServiceImpl.likePost(userId, postId);

        assertNotNull(dto);
        assertEquals(postId, dto.getPostId());
        assertEquals(userId, dto.getUserId());
        verify(postRepository).findById(postId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void testLikeComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like like = invocation.getArgument(0);
            like.setComment(comment);
            return like;
        });

        CommentLikeDto dto = likeServiceImpl.likeComment(userId, commentId);

        assertNotNull(dto);
        assertEquals(commentId, dto.getCommentId());
        assertEquals(userId, dto.getUserId());
        verify(commentRepository).findById(commentId);
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    void testUnlikePost() {
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        likeServiceImpl.unlikePost(userId, postId);

        verify(likeRepository).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void testUnlikeComment() {
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

        likeServiceImpl.unlikeComment(userId, commentId);

        verify(likeRepository).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testLikePostWhenPostNotFound() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> likeServiceImpl.likePost(userId, postId));
        assertEquals("Post with ID " + postId + " not found", exception.getMessage());
    }

    @Test
    void testLikeCommentWhenCommentNotFound() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotFoundException.class,
                () -> likeServiceImpl.likeComment(userId, commentId));
        assertEquals("Comment with ID " + commentId + " not found", exception.getMessage());
    }

    @Test
    void testCheckLikeAlreadyExistsForPost() {
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> likeServiceImpl.likePost(userId, postId));
        assertEquals("Like already exists for this post", exception.getMessage());
    }

    @Test
    void testCheckLikeAlreadyExistsForComment() {
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

        Exception exception = assertThrows(IllegalStateException.class,
                () -> likeServiceImpl.likeComment(userId, commentId));
        assertEquals("Like already exists for this comment", exception.getMessage());
    }

    @Test
    void testUnlikePostWhenLikeNotFound() {
        when(postRepository.existsById(postId)).thenReturn(true);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
                () -> likeServiceImpl.unlikePost(userId, postId));
        assertEquals("Like not found", exception.getMessage());
    }

    @Test
    void testUnlikeCommentWhenLikeNotFound() {
        when(commentRepository.existsById(commentId)).thenReturn(true);
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
                () -> likeServiceImpl.unlikeComment(userId, commentId));
        assertEquals("Like not found", exception.getMessage());
    }

    @Test
    void testUnlikePostWhenPostNotFound() {
        when(postRepository.existsById(postId)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
                () -> likeServiceImpl.unlikePost(userId, postId));
        assertEquals("Post with ID " + postId + " not found", exception.getMessage());
    }

    @Test
    void testUnlikeCommentWhenCommentNotFound() {
        when(commentRepository.existsById(commentId)).thenReturn(false);

        Exception exception = assertThrows(NotFoundException.class,
                () -> likeServiceImpl.unlikeComment(userId, commentId));
        assertEquals("Comment with ID " + commentId + " not found", exception.getMessage());
    }
}
