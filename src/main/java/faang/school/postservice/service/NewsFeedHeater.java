package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.event.heater.HeaterEvent;
import faang.school.postservice.kafka.producer.Producer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewsFeedHeater {
    private final UserServiceClient userServiceClient;
    private final Producer kafkaProducer;

    @Value("${news-feed.heater.batch-size}")
    private int batchSize;
    @Value("${spring.kafka.topic.heater}")
    private String heaterTopic;

    public void heat() {
        List<Long> followerIds = userServiceClient.getAllIds();
        List<List<Long>> splitUsers = split(followerIds, batchSize);
        splitUsers.forEach(list -> kafkaProducer.send(heaterTopic, new HeaterEvent(list)));
    }

    private List<List<Long>> split(List<Long> ids, int batchSize) {
        List<List<Long>> result = new ArrayList<>();
        int size = ids.size();
        for (int i = 0; i < size; i += batchSize) {
            result.add(ids.subList(i, Math.min(size, i + batchSize)));
        }
        return result;
    }
}
