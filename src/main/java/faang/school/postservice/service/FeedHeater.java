package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.publisher.kafka.HeatKafkaPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedHeater {
    private final UserServiceClient userServiceClient;
    private final HeatKafkaPublisher heatKafkaPublisher;

    public void heat() {
        userServiceClient.getAllUserIds()
                .forEach(heatKafkaPublisher::publish);
    }
}
