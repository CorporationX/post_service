package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaPostProducer {
	private final KafkaTemplate<String, PostEvent> postEventKafkaTemplate;
	private final SubscriptionRepository subscriptionRepository;

	@Value("${kafka.topic.posts:posts}")
	private String postsTopic;

	public void publishPostEvent(Post post) {
		List<Long> subscriberIds = subscriptionRepository.findFollowerIdsByFolloweeId(post.getAuthorId());

		PostEvent postEvent = new PostEvent(
				post.getId(),
				post.getAuthorId(),
				post.getContent(),
				subscriberIds
		);

		postEventKafkaTemplate.send(postsTopic, postEvent);
	}
}
