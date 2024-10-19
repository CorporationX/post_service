package faang.school.postservice.cosumer.kafka;

import faang.school.postservice.event.like.LikeKafkaEvent;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.repository.cache.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikesConsumer {
    @Value("${spring.data.redis.post.prefix}")
    private String prefix;
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;

    @KafkaListener(topics = "${spring.kafka.producer.topics.likes}", groupId = "${spring.kafka.consumer.group-id}")
    public void listenEvent(LikeKafkaEvent event, Acknowledgment acknowledgment) {
        if (postCacheRepository.existsById(event.getPostId())) {
            if (event.isIncrement()) {
                postCacheRepository.incrementLikes(prefix + event.getPostId());
            } else {
                postCacheRepository.decrementLikes(prefix + event.getPostId());
            }
        }
        acknowledgment.acknowledge();
    }

}
