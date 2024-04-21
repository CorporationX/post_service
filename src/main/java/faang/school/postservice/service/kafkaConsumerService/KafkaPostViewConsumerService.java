package faang.school.postservice.service.kafkaConsumerService;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.ViewEventKafka;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.repository.redis.PostRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaPostViewConsumerService extends
        AbstractKafkaConsumer<ViewEventKafka> {

    private final UserServiceClient userServiceClient;

    @Autowired
    public KafkaPostViewConsumerService(PostRedisRepository postRedisRepository, UserServiceClient userServiceClient) {
        super(postRedisRepository);
        this.userServiceClient = userServiceClient;
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.view}")
    public void addViewPost(String message) {
        ViewEventKafka viewEventKafka = listener(message, ViewEventKafka.class);
        PostHash postHash = getPostHash(viewEventKafka.getPostId());
        postHash.addView(userServiceClient.getUser(viewEventKafka.getViewId()));
        postRedisRepository.saveInRedis(postHash);
    }
}
