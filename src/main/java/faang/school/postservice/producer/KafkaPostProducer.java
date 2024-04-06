package faang.school.postservice.producer;

import faang.school.postservice.dto.event.PostEventKafka;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaPostProducer extends AbstractEventProducer<PostEventKafka> {
    @Value("${spring.kafka.topics.post.name}")
    private String postTopic;
    @Value("${spring.kafka.topics.post.cache}")
    private String topicCachePost;
    @Value("${batchSize.publish.followers}")
    private int batchSize;

    public KafkaPostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }


    public void publish(PostEventKafka postEvent) {

        List<List<Long>> partitions = ListUtils.partition(postEvent.getFollowerIds(), batchSize);

        partitions.forEach(batchFollowers -> {
            postEvent.setFollowerIds(batchFollowers);
            sendMessage(postEvent, postTopic);
        });
    }

    public void publishHeatCache(PostEventKafka postEvent) {

        List<List<Long>> partitions = ListUtils.partition(postEvent.getFollowerIds(), batchSize);

        partitions.forEach(batchFollowers -> {
            postEvent.setFollowerIds(batchFollowers);
            sendMessage(postEvent, topicCachePost);
        });
    }
}
