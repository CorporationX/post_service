package faang.school.postservice.listener;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPostConsumer {
	private final FeedService feedService;

	@KafkaListener(
			topics = "${kafka.topic.posts}",
			containerFactory = "postEventKafkaListenerContainerFactory"
	)
	public void consumePostEvent(PostEvent postEvent, Acknowledgment acknowledgment) {
		try {
			int subscriberCount = postEvent.subscriberIds() == null ? 0 : postEvent.subscriberIds().size();
			log.info("Processing post event for postId: {} with {} subscribers",
					postEvent.postId(), subscriberCount);

			int successfulUpdates = 0;
			if (postEvent.subscriberIds() != null) {
				for (Long subscriberId : postEvent.subscriberIds()) {
					try {
						feedService.addPostToFeed(subscriberId, postEvent.postId());
						successfulUpdates++;
					} catch (Exception e) {
						log.error("Failed to update feed for subscriber {}", subscriberId, e);
					}
				}
			}

			acknowledgment.acknowledge();
			log.info("Successfully processed post event for postId: {} ({} feeds updated)",
					postEvent.postId(), successfulUpdates);

		} catch (Exception e) {
			log.error("Failed to process post event for postId: {}", postEvent.postId(), e);
			throw e;
		}
	}
}
