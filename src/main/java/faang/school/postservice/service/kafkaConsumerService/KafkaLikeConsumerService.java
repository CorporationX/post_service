package faang.school.postservice.service.kafkaConsumerService;

import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.redis.PostRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaLikeConsumerService extends
        AbstractKafkaConsumer<LikeEventKafka> {

    @Autowired
    public KafkaLikeConsumerService(PostRedisRepository postRedisRepository) {
        super(postRedisRepository);
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.like}")
    public void addLike(String message) {
        LikeEventKafka likeEventKafka = listener(message, LikeEventKafka.class);

        PostHash postHash = getPostHash(likeEventKafka.getPostId());
        postHash.addLike(likeEventKafka.getLikeDto());
        postRedisRepository.saveInRedis(postHash);
    }
}
