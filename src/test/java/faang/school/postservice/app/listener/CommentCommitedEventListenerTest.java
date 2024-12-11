package faang.school.postservice.app.listener;

import faang.school.postservice.kafka.producer.AuthorCommentKafkaProducer;
import faang.school.postservice.kafka.producer.CommentKafkaProducer;
import faang.school.postservice.model.dto.CommentDto;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.event.CommentEvent;
import faang.school.postservice.model.event.application.CommentCommittedEvent;
import faang.school.postservice.model.event.kafka.AuthorCommentKafkaEvent;
import faang.school.postservice.model.event.kafka.CommentSentKafkaEvent;
import faang.school.postservice.redis.publisher.CommentEventPublisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentCommitedEventListenerTest {

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentKafkaProducer commentKafkaProducer;

    @Mock
    private AuthorCommentKafkaProducer authorCommentKafkaProducer;

    @InjectMocks
    private CommentCommitedEventListener listener;

    @Test
    void testHandleCommentCommittedEvent() {
        // Arrange
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setPostId(2L);
        commentDto.setAuthorId(3L);
        commentDto.setContent("Test comment content");

        Post post = new Post();
        post.setId(2L);
        post.setAuthorId(10L);

        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        CommentCommittedEvent event = new CommentCommittedEvent(commentDto);

        // Act
        listener.handleCommentCommittedEvent(event);

        // Assert
        // Захватываем аргумент для AuthorCommentKafkaProducer
        ArgumentCaptor<AuthorCommentKafkaEvent> authorCaptor = ArgumentCaptor.forClass(AuthorCommentKafkaEvent.class);
        verify(authorCommentKafkaProducer, times(1)).sendEvent(authorCaptor.capture());

        AuthorCommentKafkaEvent capturedAuthorEvent = authorCaptor.getValue();
        Assertions.assertEquals(3L, capturedAuthorEvent.getCommentAuthorId());

        // Захватываем аргумент для CommentKafkaProducer
        ArgumentCaptor<CommentSentKafkaEvent> commentCaptor = ArgumentCaptor.forClass(CommentSentKafkaEvent.class);
        verify(commentKafkaProducer, times(1)).sendEvent(commentCaptor.capture());

        CommentSentKafkaEvent capturedCommentEvent = commentCaptor.getValue();
        Assertions.assertEquals(2L, capturedCommentEvent.getPostId());
        Assertions.assertEquals(3L, capturedCommentEvent.getCommentAuthorId());
        Assertions.assertEquals(1L, capturedCommentEvent.getCommentId());
        Assertions.assertEquals("Test comment content", capturedCommentEvent.getCommentContent());

        // Проверка CommentEventPublisher
        verify(commentEventPublisher, times(1)).publish(any(CommentEvent.class));
    }

}
