package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaTopicResolver;
import faang.school.postservice.publisher.kafka.events.FeedHeatEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeatService {
    private final UserServiceClient userServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicResolver kafkaTopicResolver;

    @Value("${app.cache-heat.batch-size:1000}")
    private int batchSize;

    public void startCacheHeat() {
        log.info("Starting cache warm-up process.");

        List<Long> userIds = userServiceClient.getAllUserIds();
        List<List<Long>> userBatches = splitListIntoBatches(userIds, batchSize);

        for (List<Long> batch : userBatches) {
            FeedHeatEvent event = new FeedHeatEvent(batch);
            sendCacheHeatEvent(event);
        }

        log.info("Cache warm-up events sent to Kafka.");
    }

    private void sendCacheHeatEvent(FeedHeatEvent event) {
        String topic = kafkaTopicResolver.resolveTopic(event);
        kafkaTemplate.send(topic, event);
        log.info("Sent FeedHeatEvent to topic {}: {}", topic, event);
    }

    private List<List<Long>> splitListIntoBatches(List<Long> list, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        int totalSize = list.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            batches.add(list.subList(i, Math.min(totalSize, i + batchSize)));
        }
        return batches;
    }
}