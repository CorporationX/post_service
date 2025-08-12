package faang.school.postservice.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.kafka.KafkaHeatFeedSizeDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHeatFeedConsumer {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @KafkaListener(topics = "${kafka.topics.heatFeedRequest}", groupId = "my-consumer-group")
    public void counsumePostEvent(KafkaHeatFeedSizeDto kafkaHeatFeedSizeDto) {
        userContext.setUserId(1);

        Map<Long, List<Long>> followersIdsOfUser = new HashMap<>(); // User -> List<AuthorIds>

        // log.info("==HEAT FEED== \nMessage {} has been received from Kakfa. PageNumber {}, PageSize {}.", 
        //     kafkaHeatFeedSizeDto,
        //     kafkaHeatFeedSizeDto.getPageNumber(),
        //     kafkaHeatFeedSizeDto.getPageSize()
        // );

        List<Long> followerIds = userServiceClient.getFollowersPaged(
            kafkaHeatFeedSizeDto.getPageNumber(), 
            kafkaHeatFeedSizeDto.getPageSize()
        );
        log.info("==HEAT FEED== \n List of the users {}", followerIds);

        for (long userId : followerIds) {
            List<UserDto> followees = userServiceClient.getFollowing(userId);
            List<Long> followeeIds = followees.stream()
                .map(UserDto::id)
                .collect(Collectors.toList());
            followersIdsOfUser.put(userId, followeeIds);
        }

        log.info("==HEAT FEED USERS== \nUsers list ids {}.", followersIdsOfUser.size());
    }
}
