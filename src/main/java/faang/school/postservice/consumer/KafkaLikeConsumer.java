package faang.school.postservice.consumer;

import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.model.event.PostEvent;
import faang.school.postservice.repository.redis.RedisPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaLikeConsumer extends AbstractSimpleConsumer<LikeEvent> {

    public KafkaLikeConsumer(RedisPostRepository redisPostRepository) {
        super(redisPostRepository);
    }

    @KafkaListener(topics = "${spring.data.kafka.topics.like_topic}", groupId = "${spring.data.kafka.group-id}")
    public void listen(LikeEvent likeEvent, Acknowledgment acknowledgment) {
        listenEvent(likeEvent, acknowledgment);
    }

    @Override
    protected void processEvent(LikeEvent event, PostEvent post) {
        int currentCountOfLikes = post.getLikesCount();
        post.setLikesCount(currentCountOfLikes + 1);

        log.debug("Increased number of likes on post {}. Total likes: {}", post.getId(), post.getLikesCount());
    }
}
