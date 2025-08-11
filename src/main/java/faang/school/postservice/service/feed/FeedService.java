package faang.school.postservice.service.feed;

// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaHeatMessageSizeConfig;
import faang.school.postservice.config.kafka.KafkaTopicsConfig;
import faang.school.postservice.dto.kafka.KafkaHeatFeedSizeDto;
// import faang.school.postservice.dto.user.UserDto;
// import faang.school.postservice.mapper.redis.RedisPostMapper;
// import faang.school.postservice.model.RedisPost;
import faang.school.postservice.publisher.post.kafka.KafkaHeatFeadProducer;
// import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final UserServiceClient userServiceClient;
    // private final PostRepository postRepository;
    // private final RedisPostMapper redisPostMapper;
    private final KafkaHeatFeadProducer kafkaHeatFeadProducer;
    private final KafkaTopicsConfig kafkaTopicsConfig;
    private final KafkaHeatMessageSizeConfig kafkaHeatMessageSizeConfig;

    public void heat() {
        int authorsCount = userServiceClient.getAllAuthors();
        int subscribersCount = userServiceClient.getAllSubscribers();

        int authorsPages = getNubmerOfPages(authorsCount, kafkaHeatMessageSizeConfig.getHeatPostPageSize());
        int subscribersPages = getNubmerOfPages(subscribersCount, kafkaHeatMessageSizeConfig.getHeatFeedPageSize());

        // send feed heat event
        for (int i = 0; i < subscribersPages; i++) {
            kafkaHeatFeadProducer.sendMessage(
                new KafkaHeatFeedSizeDto(i, kafkaHeatMessageSizeConfig.getHeatFeedPageSize()), 
                kafkaTopicsConfig.getHeatFeedRequest()
            );
        }

        // send post heat event
        for (int i = 0; i < authorsPages; i++) {
            kafkaHeatFeadProducer.sendMessage(
                new KafkaHeatFeedSizeDto(i, kafkaHeatMessageSizeConfig.getHeatPostPageSize()), 
                kafkaTopicsConfig.getHeatPostRequest()
            );
        }

        // send user heat event
        for (int i = 0; i < authorsPages; i++) {
            kafkaHeatFeadProducer.sendMessage(
                new KafkaHeatFeedSizeDto(i, kafkaHeatMessageSizeConfig.getHeatUserPageSize()), 
                kafkaTopicsConfig.getHeatPostRequest()
            );
        }
    }

    private int getNubmerOfPages(int totalRecords, int pageSize) {
        return (int) Math.ceil((double) totalRecords / pageSize);
    }

    // private void techHeat() {
    //     int authorsCount = userServiceClient.getAllAuthors();
    //     int subscribersCount = userServiceClient.getAllSubscribers();
    //     List<Integer> authors = userServiceClient.getAuthorsOrderdBySubscribers();
    //     Map<Long, List<RedisPost>> postsOfUser = new HashMap<>();
    //     Map<Long, List<UserDto>> followersOfUser = new HashMap<>();
    //     Map<Long, List<Long>> followersIdsOfUser = new HashMap<>();

    //     for (long userId : authors) {
    //         followersOfUser.computeIfAbsent(userId, k -> userServiceClient.getFollowers(userId));
    //         postsOfUser.computeIfAbsent(userId, k -> postRepository.findByAuthorId(userId)
    //             .stream()
    //             .map(redisPostMapper::toRedisPost)
    //             .toList()
    //         );
    //     }

    //     followersIdsOfUser = followersOfUser.entrySet().stream()
    //         .collect(Collectors.<Map.Entry<Long, List<UserDto>>, Long, List<Long>>toMap(
    //             Map.Entry::getKey,
    //             entry -> entry.getValue().stream()
    //                 .map(UserDto::id)
    //                 .collect(Collectors.toList())
    //     ));        
        
    //     log.info("Total lines to process Authors {}.", authorsCount);
    //     log.info("Total lines to process Subscribers {}.", subscribersCount);
    //     log.info("List of authors by subscribers {}.", authors);
    //     log.info("Users list {}.", followersOfUser);
    //     log.info("Users and posts {}.", postsOfUser);
    //     log.info("users list ids {}.", followersIdsOfUser);
    // }
}