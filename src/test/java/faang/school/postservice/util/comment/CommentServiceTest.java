package faang.school.postservice.util.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Post post;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        comment = Comment.builder()
                .id(1L)
                .authorId(10L)
                .content("Test comment")
                .updatedAt(LocalDateTime.now())
                .build();

        post = Post.builder()
                .id(1L)
                .content("Test post")
                .comments(new ArrayList<>())
                .build();
    }

    @Test
    public void testCreateComment() {
        when(userServiceClient.getUser(comment.getAuthorId())).thenReturn(userDto);
        when(postService.getPostById(1L)).thenReturn(post);
        when(commentRepository.save(comment)).thenReturn(comment);

        Comment result = commentService.createComment(comment, 1L);

        assertNotNull(result);
        assertEquals(post, result.getPost());
        verify(userServiceClient).getUser(comment.getAuthorId());
        verify(postService).getPostById(1L);
        verify(commentRepository).save(comment);
    }

    @Test
    public void testCreateCommentUserNotFound() {
        when(userServiceClient.getUser(comment.getAuthorId())).thenThrow(new NoSuchElementException("User not found"));

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.createComment(comment, 1L);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(userServiceClient).getUser(comment.getAuthorId());
        verify(postService, never()).getPostById(anyLong());
        verify(commentRepository, never()).save(any());
    }

    @Test
    public void testUpdateCommentCommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.updateComment(comment);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(commentRepository).findById(1L);
    }

    @Test
    public void testUpdateComment() {
        Comment updatedComment = Comment.builder()
                .id(1L)
                .authorId(10L)
                .content("Updated content")
                .updatedAt(LocalDateTime.now())
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        Comment result = commentService.updateComment(updatedComment);

        assertNotNull(result);
        assertEquals(updatedComment.getContent(), result.getContent());
        verify(commentRepository).findById(1L);
    }

    @Test
    public void testGetAllCommentsToPost() {
        post.getComments().add(comment);
        when(postService.getPostById(1L)).thenReturn(post);

        List<Comment> comments = commentService.getAllCommentsToPost(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        verify(postService).getPostById(1L);
    }

    @Test
    public void testGetAllCommentsToPostPostNotFound() {
        when(postService.getPostById(1L)).thenThrow(new NoSuchElementException("Post not found"));

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.getAllCommentsToPost(1L);
        });

        assertEquals("Post not found", exception.getMessage());
        verify(postService).getPostById(1L);
    }

    @Test
    public void testDeleteComment() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository).findById(1L);
        verify(commentRepository).deleteById(1L);
    }

    @Test
    public void testDeleteCommentCommentNotFound() {
        when(commentRepository.findById(1L)).thenThrow(new NoSuchElementException("Comment not found"));

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            commentService.deleteComment(1L);
        });

        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository).findById(1L);
    }
}