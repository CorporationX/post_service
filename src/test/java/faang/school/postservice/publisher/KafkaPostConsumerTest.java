package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostCreatedEventDto;
import faang.school.postservice.service.FeedService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPostConsumerTest {

    @Mock
    private FeedService feedService;

    @InjectMocks
    private KafkaPostConsumer consumer;

    @Test
    @DisplayName("onPostCreated should add post to all follower feeds and acknowledge message")
    void onPostCreated_shouldProcessAllFollowers_andAck() {

        PostCreatedEventDto event = buildEvent(List.of(11L, 22L, 33L));
        Acknowledgment ack = mock(Acknowledgment.class);

        consumer.onPostCreated(event, ack);

        for (Long followerId : event.followerIds()) {
            verify(feedService).addPostToFeed(followerId, event.postId(), event.occurredAt());
        }
        verify(ack).acknowledge();
        verifyNoMoreInteractions(ack);
    }

    @Test
    @DisplayName("onPostCreated should call FeedService once per follower")
    void onPostCreated_shouldCallFeedServicePerFollower() {

        PostCreatedEventDto event = buildEvent(List.of(1L, 2L, 3L, 4L));
        Acknowledgment ack = mock(Acknowledgment.class);

        consumer.onPostCreated(event, ack);

        verify(feedService, times(4)).addPostToFeed(anyLong(), eq(event.postId()), eq(event.occurredAt()));
        verify(ack).acknowledge();
    }

    @Test
    @DisplayName("onPostCreated should not acknowledge Kafka message when FeedService throws exception")
    void onPostCreated_shouldNotAck_whenException() {

        PostCreatedEventDto event = buildEvent(List.of(10L, 20L));
        Acknowledgment ack = mock(Acknowledgment.class);

        doThrow(new RuntimeException("boom"))
                .when(feedService)
                .addPostToFeed(eq(10L), eq(event.postId()), eq(event.occurredAt()));

        assertThrows(RuntimeException.class, () -> consumer.onPostCreated(event, ack));

        verify(feedService).addPostToFeed(10L, event.postId(), event.occurredAt());
        verify(ack, never()).acknowledge();
    }

    @Test
    @DisplayName("onPostCreated should handle event with empty followers list")
    void onPostCreated_shouldAck_whenNoFollowers() {

        PostCreatedEventDto event = buildEvent(List.of());
        Acknowledgment ack = mock(Acknowledgment.class);

        consumer.onPostCreated(event, ack);

        verifyNoInteractions(feedService);
        verify(ack).acknowledge();
    }

    private static PostCreatedEventDto buildEvent(List<Long> followers) {
        return new PostCreatedEventDto(
                UUID.randomUUID(),
                Instant.parse("2025-12-28T00:00:00Z"),
                999L,
                null, // Publisher - keep null unless you need it
                followers,
                1
        );
    }
}
