package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.kafka.producer.KafkaSender;
import faang.school.postservice.repository.feed.FeedCacheRepository;
import faang.school.postservice.service.post.PostService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedCacheRepository feedCacheRepository;
    @Value("${heater.partitions:1000}")
    private int partitionSize;
    @Value("${cache.memory.max-feed-size:500}")
    private int feedSize;
    @Value("${spring.kafka.topics.feed.name:feed}")
    private String feedTopicName;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final KafkaSender kafkaSender;
    private final UserContext userContext;

    @Transactional
    public List<FeedDto> heat() {
        userContext.setUserId(userContext.getUserId());
        var feedList = userServiceClient.getUserFollowees().parallelStream()
                .map(userFollowees -> FeedDto.builder()
                        .userId(userFollowees.getUserId().toString())
                        .postIds(postService.getLastFolloweePostIds(userFollowees.getFolloweeIds(), feedSize))
                        .build())
                .toList();
        ListUtils.partition(feedList, partitionSize).forEach(listOfFeeds -> {
            kafkaSender.sendMessage(feedTopicName, listOfFeeds);
        });
        return feedList;
    }

    @SneakyThrows
    public FeedDto findById(@NotNull String userId) {
        return feedCacheRepository.findById(userId)
                .orElse(FeedDto.builder()
                        .userId(userId)
                        .postIds(new ArrayList<>())
                        .build());
    }
}
