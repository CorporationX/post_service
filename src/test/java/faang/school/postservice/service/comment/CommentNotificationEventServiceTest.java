package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentNotificationEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.RedisCommentNotificationEventPublisher;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentNotificationEventServiceTest {
    @Mock
    private PostService postService;
    @Mock
    private RedisCommentNotificationEventPublisher commentNotificationEventPublisher;
    @InjectMocks
    private CommentNotificationEventService commentNotificationEventService;

    private Long postId;
    private Post post;
    private Comment comment;

    @BeforeEach
    void setUp() {
        postId = 1L;
        post = new Post();
        post.setId(postId);

        comment = new Comment();
        comment.setId(2L);
        comment.setContent("Sample Comment");
    }

    @Test
    void shouldPublishEventWhenCommentIsNotFromPostAuthor() {
        Long postAuthorId = 10L;
        Long commentAuthorId = 20L;

        post.setAuthorId(postAuthorId);
        comment.setAuthorId(commentAuthorId);

        when(postService.findPostById(postId)).thenReturn(post);

        commentNotificationEventService.handleCommentEvent(postId, comment);

        verify(commentNotificationEventPublisher).publishCommentNotificationEvent(any(CommentNotificationEvent.class));
    }

    @Test
    void shouldNotPublishEventWhenCommentIsFromPostAuthor() {
        Long postAuthorId = 10L;

        post.setAuthorId(postAuthorId);
        comment.setAuthorId(postAuthorId);

        when(postService.findPostById(postId)).thenReturn(post);

        commentNotificationEventService.handleCommentEvent(postId, comment);

        verify(commentNotificationEventPublisher, never()).publishCommentNotificationEvent(any(CommentNotificationEvent.class));
    }
}