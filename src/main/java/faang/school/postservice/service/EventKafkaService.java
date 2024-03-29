package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.HeatFeedKafkaEventDto;
import faang.school.postservice.dto.event.PostKafkaEventDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaHeatFeedProducer;
import faang.school.postservice.publisher.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventKafkaService {
    private final UserServiceClient userServiceClient;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaHeatFeedProducer kafkaHeatFeedProducer;

    @Value("${spring.kafka.topics.post.batch-size}")
    private int followersBatchSize;
    @Value("${spring.kafka.topics.heat-feed.batch-size}")
    private int usersBatchSize;

    //Запускаем отправку события в кафку асинхронно, чтобы транзакция в PostService продолжала выполняться
//    @Async(value = "executorService")
    public void sendPostEvent(Post post) {
        List<Long> followers = userServiceClient.getFollowers(post.getAuthorId());
        if (followers.isEmpty()) {
            return;
        }
        List<List<Long>> batchFollowers = ListUtils.partition(followers, followersBatchSize);
        batchFollowers.forEach(portionFollowers -> {
            PostKafkaEventDto event = new PostKafkaEventDto(post.getId(), portionFollowers);
            kafkaPostProducer.sendAsyncPostEvent(event);
        });
    }

    public void sendHeatFeedEvent() {
        List<Long> userIds = userServiceClient.getUserIds();
        List<List<Long>> partitionUserIds = ListUtils.partition(userIds, usersBatchSize);
        partitionUserIds.forEach(portionUsers ->
                kafkaHeatFeedProducer.sendAsyncHeatFeedEvent(new HeatFeedKafkaEventDto(portionUsers)));
    }
}