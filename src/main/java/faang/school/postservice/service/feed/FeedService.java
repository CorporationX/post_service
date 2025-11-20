package faang.school.postservice.service.feed;

import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaHeatMessageSizeConfig;
import faang.school.postservice.config.kafka.KafkaTopicsConfig;
import faang.school.postservice.dto.kafka.KafkaHeatFeedSizeDto;
import faang.school.postservice.publisher.post.kafka.KafkaHeatFeadProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;
    private final KafkaHeatFeadProducer kafkaHeatFeadProducer;
    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final KafkaHeatMessageSizeConfig kafkaHeatMessageSizeConfig;

    public void heat() {
        int subscribersCount = userServiceClient.getAllSubscribers();
        int autorsCount = userServiceClient.getAllAuthors();

        int subscribersPages = getNubmerOfPages(subscribersCount, kafkaHeatMessageSizeConfig.getHeatFeedPageSize());
        int authorsPages = getNubmerOfPages(autorsCount, kafkaHeatMessageSizeConfig.getHeatPostPageSize());

        // pagination for all users subscribers
        for (int i = 0; i < subscribersPages; i++) {
            kafkaHeatFeadProducer.sendMessage(
                new KafkaHeatFeedSizeDto(i, kafkaHeatMessageSizeConfig.getHeatFeedPageSize()), 
                kafkaTopicsConfig.getHeatFeedRequest()
            );
        }

        // pagination for all users autors
        for (int i = 0; i < authorsPages; i++) {
            kafkaHeatFeadProducer.sendMessage(
                new KafkaHeatFeedSizeDto(i, kafkaHeatMessageSizeConfig.getHeatPostPageSize()), 
                kafkaTopicsConfig.getHeatPostRequest()
            );
        }
    }

    private int getNubmerOfPages(int totalRecords, int pageSize) {
        return (int) Math.ceil((double) totalRecords / pageSize);
    }
}