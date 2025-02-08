package faang.school.postservice.consumer;

import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.model.event.PostViewEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaPostViewConsumer extends AbstractSimpleConsumer<PostViewEvent> {

    public KafkaPostViewConsumer(RedisPostRepository redisPostRepository) {
        super(redisPostRepository);
    }

    @KafkaListener(topics = "${spring.data.kafka.topics.post_view_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(PostViewEvent postViewEvent, Acknowledgment acknowledgment) {
        listenEvent(postViewEvent, acknowledgment);
    }

    @Override
    protected void processEvent(PostViewEvent event, PostEvent post) {
        int currentCountOfViews = post.getViewsCount();
        post.setViewsCount(currentCountOfViews + 1);

        log.debug("Increased number of views on post {}. Total views: {}", post.getId(), post.getViewsCount());

    }
}

