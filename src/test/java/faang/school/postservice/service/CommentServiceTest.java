package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private Comment comment;
    private Post post;
    private final Long POST_ID = 1L;
    private final Long AUTHOR_ID = 1L;
    private final Long COMMENT_ID = 1L;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(POST_ID)
                .build();
        comment = Comment.builder()
                .id(COMMENT_ID)
                .content("exampleContent")
                .post(post)
                .build();
    }

    @Test
    public void getCommentsByPostIdTest() {
        Comment comment1 = Comment.builder()
                .createdAt(LocalDateTime.of(2002, 9, 4, 0, 0))
                .build();
        Comment comment2 = Comment.builder()
                .createdAt(LocalDateTime.of(2002, 1, 1, 0, 0))
                .build();
        List<Comment> comments = List.of(comment1, comment2);
        List<Comment> expected = List.of(comment1, comment2).stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();

        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<Comment> result = commentService.getCommentsByPostId(POST_ID);

        assertEquals(expected, result);
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(postService).getPostById(POST_ID);
    }

    @Test
    public void createCommentTest() {
        when(postService.getPostById(POST_ID)).thenReturn(post);

        commentService.createComment(comment, POST_ID, AUTHOR_ID);

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verify(commentRepository).save(commentCaptor.capture());

        Comment captured = commentCaptor.getValue();
        assertEquals(post, captured.getPost());
        assertEquals(AUTHOR_ID, captured.getAuthorId());
    }

    @Test
    public void createCommentTest_throwsUserNotFoundException() {
        when(userServiceClient.getUser(AUTHOR_ID)).thenThrow(FeignException.NotFound.class);
        when(postService.getPostById(POST_ID)).thenReturn(post);

        assertThrows(UserNotFoundException.class, () ->
                commentService.createComment(comment, POST_ID, AUTHOR_ID));
        verify(postService).getPostById(POST_ID);
    }

    @Test
    public void updateCommentTest() {
        Comment updatedComment = Comment.builder()
                .content("updated")
                .build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.updateComment(COMMENT_ID, updatedComment, AUTHOR_ID);

        verify(commentValidator).validateAuthor(comment, AUTHOR_ID);
        verify(commentValidator).validateCommentUpdate(updatedComment);
        verify(commentRepository).save(commentCaptor.capture());

        Comment actual = commentCaptor.getValue();
        assertEquals(updatedComment.getContent(), actual.getContent());
    }

    @Test
    public void updateCommentTest_throwsCommentNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.updateComment(COMMENT_ID, comment, AUTHOR_ID));
    }

    @Test
    public void deleteCommentTest() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.deleteComment(COMMENT_ID, AUTHOR_ID);

        verify(commentValidator).validateAuthor(comment, AUTHOR_ID);
        verify(commentRepository).delete(comment);
    }

    @Test
    public void deleteCommentTest_throwsCommentNotFoundException() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(CommentNotFoundException.class, () ->
                commentService.deleteComment(COMMENT_ID, AUTHOR_ID));
    }
}

