package faang.school.postservice.service.kafkaConsumerService;

import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.dto.hash.FeedHash;
import faang.school.postservice.repository.redis.FeedRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaPostConsumerService extends
        AbstractKafkaConsumer<PostEventKafka> {

    private final FeedRedisRepository feedRedisRepository;

    @Autowired
    public KafkaPostConsumerService(PostRedisRepository postRedisRepository, FeedRedisRepository feedRedisRepository) {
        super(postRedisRepository);
        this.feedRedisRepository = feedRedisRepository;
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.post}")
    @Async("executor")
    public void addPost(String message) {
        PostEventKafka postEventKafka = listener(message, PostEventKafka.class);
        addPostForFollowersFeed(postEventKafka);
    }

    public void addPostForFollowersFeed(PostEventKafka postEventKafka) {
        postEventKafka.getFollowersId().forEach(followerId ->
        {
            feedRedisRepository.findById(followerId).ifPresentOrElse(
                    feedHash -> {
                        feedHash.addPost(postEventKafka.getId());
                        feedRedisRepository.saveInRedis(feedHash);
                    },
                    () -> {
                        FeedHash newFeedHash = new FeedHash(followerId, postEventKafka.getId());
                        feedRedisRepository.saveInRedis(newFeedHash);
                    });
        });
    }
}
