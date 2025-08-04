package faang.school.postservice.kafka;

import faang.school.postservice.kafka.consumer.KafkaCommentConsumer;
import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.kafka.service.CommentProcessingService;
import faang.school.postservice.service.redis.dto.CommentCacheDto;
import faang.school.postservice.service.redis.entity.PostRedis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaCommentConsumerTest {

    @Mock
    private CommentProcessingService commentProcessingService;

    @Mock
    private Acknowledgment ack;

    @InjectMocks
    private KafkaCommentConsumer consumer;

    @Captor
    private ArgumentCaptor<PostRedis> postCaptor;

    private CommentEvent event;
    private CommentCacheDto dto;
    private PostRedis existing;

    @BeforeEach
    void setUp() {
        event = new CommentEvent();
        event.setId(10L);
        event.setPostId(42L);
        event.setAuthorId(7L);
        event.setContent("hello");
        event.setTimestamp(LocalDateTime.of(2025, 7, 27, 12, 0));

        dto = new CommentCacheDto();
        dto.setId(10L);
        dto.setAuthorId(7L);
        dto.setContent("hello");
        dto.setTimestamp(event.getTimestamp());
        dto.setLikesCount(0L);

        existing = new PostRedis();
        existing.setId(42L);
        existing.setContent("post");
        existing.setAuthorId(1L);
        existing.setLikesCount(0L);
        existing.setLastComments(new TreeSet<>());

    }

    @Test
    void whenProcessingSucceeds_shouldAcknowledge() {
        doNothing().when(commentProcessingService).addCommentToCache(event);

        consumer.onCommentEvent(event, ack);

        verify(commentProcessingService).addCommentToCache(event);
        verify(ack).acknowledge();
    }

    @Test
    void whenProcessingThrows_shouldNotAcknowledge() {
        doThrow(new RuntimeException("boom")).when(commentProcessingService).addCommentToCache(event);

        assertThrows(RuntimeException.class, () -> consumer.onCommentEvent(event, ack));
        verify(commentProcessingService).addCommentToCache(event);
        verify(ack, never()).acknowledge();
    }
}
