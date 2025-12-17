package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.service.feed.FeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaPostConsumerTest {

	@Mock
	private FeedService feedService;

	@Mock
	private Acknowledgment acknowledgment;

	@InjectMocks
	private KafkaPostConsumer kafkaPostConsumer;

	private PostEvent postEvent;
	private final Long postId = 1L;
	private final List<Long> subscriberIds = List.of(101L, 102L, 103L);

	@BeforeEach
	void setUp() {
		postEvent = new PostEvent(
				postId,
				100L,
				"Test content",
				subscriberIds
		);
	}

	@Test
	void consumePostEvent_success() {
		kafkaPostConsumer.consumePostEvent(postEvent, acknowledgment);

		ArgumentCaptor<Long> subscriberIdCaptor = ArgumentCaptor.forClass(Long.class);
		ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);

		verify(feedService, times(3)).addPostToFeed(
				subscriberIdCaptor.capture(),
				postIdCaptor.capture()
		);

		List<Long> capturedSubscriberIds = subscriberIdCaptor.getAllValues();
		List<Long> capturedPostIds = postIdCaptor.getAllValues();

		assertEquals(subscriberIds, capturedSubscriberIds);
		assertEquals(List.of(postId, postId, postId), capturedPostIds);

		verify(acknowledgment).acknowledge();
	}

	@Test
	void consumePostEvent_emptySubscribers() {
		PostEvent eventWithNoSubscribers = new PostEvent(
				2L, 100L, "Content", List.of()
		);

		kafkaPostConsumer.consumePostEvent(eventWithNoSubscribers, acknowledgment);

		verify(feedService, never()).addPostToFeed(anyLong(), anyLong());
		verify(acknowledgment).acknowledge();
	}

	@Test
	void consumePostEvent_nullSubscribers() {
		PostEvent eventWithNullSubscribers = new PostEvent(
				3L, 100L, "Content", null
		);

		kafkaPostConsumer.consumePostEvent(eventWithNullSubscribers, acknowledgment);

		verify(feedService, never()).addPostToFeed(anyLong(), anyLong());
		verify(acknowledgment).acknowledge();
	}

	@Test
	void consumePostEvent_feedServiceThrowsException() {
		doThrow(new RuntimeException("Redis error")).when(feedService).addPostToFeed(anyLong(), anyLong());

		kafkaPostConsumer.consumePostEvent(postEvent, acknowledgment);

		verify(feedService, times(3)).addPostToFeed(anyLong(), anyLong());
		verify(acknowledgment).acknowledge();
	}

	@Test
	void consumePostEvent_mixedSuccessAndFailure() {
		doNothing().when(feedService).addPostToFeed(101L, postId);
		doThrow(new RuntimeException("Redis error")).when(feedService).addPostToFeed(102L, postId);
		doThrow(new RuntimeException("Redis error")).when(feedService).addPostToFeed(103L, postId);

		kafkaPostConsumer.consumePostEvent(postEvent, acknowledgment);

		verify(feedService, times(3)).addPostToFeed(anyLong(), anyLong());
		verify(acknowledgment).acknowledge();
	}
}