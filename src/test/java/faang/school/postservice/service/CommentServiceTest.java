package faang.school.postservice.service;

import faang.school.postservice.events.CommentEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @InjectMocks
    private CommentService commentService;

    @Test
    void shouldCreateCommentAndPublishEvent() {
        Long postId = 1L;
        Long authorId = 2L;
        Long postAuthorId = 3L;
        String content = "Test comment";

        Post post = Post.builder()
                .id(postId)
                .authorId(postAuthorId)
                .build();

        Comment comment = Comment.builder()
                .id(10L)
                .post(post)
                .authorId(authorId)
                .content(content)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.createComment(postId, authorId, content);

        assertEquals(comment.getId(), result.getId());

        ArgumentCaptor<faang.school.postservice.events.CommentEvent> eventCaptor =
                ArgumentCaptor.forClass(faang.school.postservice.events.CommentEvent.class);
        verify(commentEventPublisher).publishCommentEvent(eventCaptor.capture());

        faang.school.postservice.events.CommentEvent capturedEvent = eventCaptor.getValue();
        assertEquals(10L, capturedEvent.commentId());
        assertEquals(authorId, capturedEvent.commentAuthorId());
        assertEquals(postId, capturedEvent.postId());
        assertEquals(postAuthorId, capturedEvent.postAuthorId());
        assertEquals(content, capturedEvent.commentText());
        assertNotNull(capturedEvent.createdAt());
    }

    @Test
    void shouldNotPublishEventWhenCommentAuthorIsPostAuthor() {
        Long postId = 1L;
        Long authorId = 2L;
        String content = "Test comment";

        Post post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();

        Comment comment = Comment.builder()
                .id(10L)
                .post(post)
                .authorId(authorId)
                .content(content)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.createComment(postId, authorId, content);

        verify(commentEventPublisher, never()).publishCommentEvent(any());
    }
}
