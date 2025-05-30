package faang.school.postservice.newsfeed.consumer;


import faang.school.postservice.dto.newsfeed.KafkaCommentEvent;
import faang.school.postservice.service.newsfeed.RedisCacheService;
import org.springframework.stereotype.Component;

@Component
public class KafkaCommentConsumer extends AbstractKafkaConsumer<KafkaCommentEvent> {

    protected KafkaCommentConsumer(RedisCacheService redisCacheService) {
        super(redisCacheService);
    }

    @Override
    protected void processEvent(KafkaCommentEvent event) {
        redisCacheService.addCommentToPost(event);
    }

    @Override
    public String getTopic() {
        return "comments";
    }
}
