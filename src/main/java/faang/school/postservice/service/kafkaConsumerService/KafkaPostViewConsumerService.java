package faang.school.postservice.service.kafkaConsumerService;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.ViewEventKafka;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.repository.redis.PostRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaPostViewConsumerService extends
        AbstractKafkaConsumer<ViewEventKafka> {

    @Autowired
    public KafkaPostViewConsumerService(PostRedisRepository postRedisRepository) {
        super(postRedisRepository);
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.view}")
    @Async("executor")
    public void addViewPost(String message) {
        ViewEventKafka viewEventKafka = listener(message, ViewEventKafka.class);
        PostHash postHash = getPostHash(viewEventKafka.getPostId());
        postHash.addView(viewEventKafka.getUserDto());
        postRedisRepository.saveInRedis(postHash);
    }
}
