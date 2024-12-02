package faang.school.postservice.publisher.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.event.kafka.HeatTaskEventKafka;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaHeatTaskProducer extends AbstractKafkaEventProducer<HeatTaskEventKafka> {

    @Value("${cache.heat.producer-batch-size}")
    private int batchSize;

    UserServiceClient userServiceClient;

    public KafkaHeatTaskProducer(KafkaTemplate<String, Object> multiTypeKafkaTemplate,
                                 @Value("${spring.kafka.topics.cache-heat}") String kafkaTopic,
                                 UserServiceClient userServiceClient) {
        super(multiTypeKafkaTemplate, kafkaTopic);
        this.userServiceClient = userServiceClient;
    }

    public void publishHeatTasks () {
        long maxUserId = userServiceClient.getMaxUserId();

        for (long startId = 1; startId <= maxUserId; startId += batchSize) {
            long endId = Math.min(startId + batchSize - 1, maxUserId);
            HeatTaskEventKafka task = new HeatTaskEventKafka(startId, endId);

            try {
                sendEvent(task);
                log.info("Published heat task for userId range: {}-{}", startId, endId);
            } catch (KafkaException e) {
                log.error("Failed to publish heat task for userId range: {}-{}", startId, endId, e);
            }
        }
    }
}
