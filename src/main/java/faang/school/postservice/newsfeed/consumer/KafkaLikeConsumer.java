package faang.school.postservice.newsfeed.consumer;

import faang.school.postservice.dto.newsfeed.KafkaLikeEvent;
import faang.school.postservice.service.newsfeed.RedisCacheService;
import org.springframework.stereotype.Component;

@Component
public class KafkaLikeConsumer extends AbstractKafkaConsumer<KafkaLikeEvent> {

    protected KafkaLikeConsumer(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @Override
    protected void processEvent(KafkaLikeEvent event) {
        redisCacheService.addLikeToPost(event);
    }

    @Override
    public String getTopic() {
        return "likes";
    }
}
