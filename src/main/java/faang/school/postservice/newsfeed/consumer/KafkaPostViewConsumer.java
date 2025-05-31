package faang.school.postservice.newsfeed.consumer;

import faang.school.postservice.dto.newsfeed.KafkaPostViewEvent;
import faang.school.postservice.service.newsfeed.RedisCacheService;
import org.springframework.stereotype.Component;

@Component
public class KafkaPostViewConsumer extends AbstractKafkaConsumer<KafkaPostViewEvent> {

    protected KafkaPostViewConsumer(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @Override
    protected void processEvent(KafkaPostViewEvent event) {
        redisCacheService.addViewToPost(event);
    }

    @Override
    public String getTopic() {
        return "postViews";
    }
}
