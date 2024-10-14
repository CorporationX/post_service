package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.RedisCommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private PostService postService;
    @Mock
    private RedisCommentEventPublisher commentEventPublisher;
    @Captor
    private ArgumentCaptor<CommentEvent> commentEventCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;
    @InjectMocks
    private CommentService commentService;

    private static final Long postId = 1L;
    private static final Long commentId = 1L;
    private static final Long authorId = 1L;
    private static final Long invalidAuthorId = 2L;
    private static final String content = "content";
    private static final String newContent = "new content";
    private Comment found;
    private Comment newComment;

    @BeforeEach
    void init() {
        found = Comment.builder().id(commentId).content(content).authorId(authorId).build();
        newComment = Comment.builder().content(newContent).build();
    }

    @Test
    void testCreateComment() {
        Long postAuthorId = 3L;
        Post post = Post.builder().id(postId).authorId(postAuthorId).build();
        Comment comment = Comment.builder().authorId(authorId).content(content).build();
        Comment savedComment = Comment.builder().id(10L).authorId(authorId).content(content).post(post).build();

        when(postService.findPostById(postId)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        commentService.createComment(postId, comment);

        verify(commentValidator).validateCreate(postId, comment);

        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(postId, commentCaptor.getValue().getPost().getId());
        assertEquals(content, commentCaptor.getValue().getContent());

        verify(commentEventPublisher).publishCommentEvent(commentEventCaptor.capture());

        CommentEvent capturedEvent = commentEventCaptor.getValue();
        assertEquals(postId, capturedEvent.getPostId());
        assertEquals(authorId, capturedEvent.getAuthorId());
        assertEquals(savedComment.getId(), capturedEvent.getCommentId());
        assertNotNull(capturedEvent.getTimestamp());
    }

    @Test
    void testCreateCommentThrowsValidationException() {
        doThrow(new ValidationException("error"))
                .when(commentValidator).validateCreate(postId, newComment);
        assertThrows(
                ValidationException.class,
                () -> commentService.createComment(postId, newComment)
        );
    }

    @Test
    void testUpdateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));

        commentService.updateComment(commentId, newComment);

        verify(commentRepository).save(commentCaptor.capture());
        assertEquals(commentId, commentCaptor.getValue().getId());
        assertEquals(newContent, commentCaptor.getValue().getContent());
    }

    @Test
    void testUpdateCommentThrowsValidationException() {
        newComment.setAuthorId(invalidAuthorId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));
        doThrow(ValidationException.class)
                .when(commentValidator).validateCommentAuthorId(invalidAuthorId, found);

        assertThrows(
                ValidationException.class,
                () -> commentService.updateComment(commentId, newComment)
        );
    }

    @Test
    void testGetAllCommentsByPostId() {
        var comment1 = Comment.builder()
                .id(1L)
                .createdAt(LocalDateTime.of(2024, 1, 1, 1, 1,1))
                .build();
        var comment2 = Comment.builder()
                .id(2L)
                .createdAt(LocalDateTime.of(2024, 2, 1, 1, 1,1))
                .build();
        var comment3 = Comment.builder()
                .id(3L)
                .createdAt(LocalDateTime.of(2024, 3, 1, 1, 1,1))
                .build();
        List<Comment> comments = List.of(comment1, comment2, comment3);
        List<Comment> sortedComments = comments.stream()
                .sorted(
                        (c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt())
                )
                .toList();

        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        var actual = commentService.getAllCommentsByPostId(postId);
        assertEquals(sortedComments, actual);
    }

    @Test
    void testDelete() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));
        commentService.delete(commentId);
        Mockito.verify(commentRepository).deleteById(commentId);
    }

    @Test
    void testDeleteThrowsCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.delete(commentId)
        );
    }

    @Test
    void testGetById() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(found));
        var actual = commentService.getById(commentId);
        assertEquals(found, actual);
    }

    @Test
    void testGetByIdThrowsCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        assertThrows(
                CommentNotFoundException.class,
                () -> commentService.getById(commentId)
        );
    }
}