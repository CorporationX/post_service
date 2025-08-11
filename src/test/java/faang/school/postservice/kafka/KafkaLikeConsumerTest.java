package faang.school.postservice.kafka;

import faang.school.postservice.kafka.consumer.KafkaLikeConsumer;
import faang.school.postservice.kafka.event.LikeEvent;
import faang.school.postservice.kafka.service.LikeProcessingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaLikeConsumerTest {

    @Mock
    LikeProcessingService likeProcessingService;

    @Mock
    Acknowledgment ack;

    @InjectMocks
    KafkaLikeConsumer consumer;

    private static LikeEvent event(long postId) {
        LikeEvent e = new LikeEvent();
        e.setId(1L);
        e.setPostId(postId);
        e.setAuthorId(7L);
        e.setTimestamp(LocalDateTime.now());
        return e;
    }

    @Test
    void acksOnSuccess() {
        LikeEvent e = event(123L);

        consumer.onLikeEvent(e, ack);

        verify(likeProcessingService).addLikeToCache(e);
        verify(ack).acknowledge();
    }

    @Test
    void doesNotAckOnFailureAndRethrows() {
        LikeEvent e = event(456L);
        doThrow(new RuntimeException("boom")).when(likeProcessingService).addLikeToCache(any());

        assertThrows(RuntimeException.class, () -> consumer.onLikeEvent(e, ack));
        verify(ack, never()).acknowledge();
    }
}
