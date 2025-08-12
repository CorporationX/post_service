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
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.RecentPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHeatFeedConsumer {
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostRepository PostRepository;
    private final RecentPostService recentPostService;

    @KafkaListener(topics = "${kafka.topics.heatFeedRequest}", groupId = "my-consumer-group")
    public void counsumePostEvent(KafkaHeatFeedSizeDto kafkaHeatFeedSizeDto) {
        userContext.setUserId(1);

        Map<Long, List<Long>> followersIdsOfUser = new HashMap<>(); // Subscriber -> List<AuthorIds>
        Map<Long, List<Long>> followersPostIds = new HashMap<>(); // Subscriber -> List<PostId>

        // getting the portion of users (subscribers)
        List<Long> followerIds = userServiceClient.getFollowersPaged(
            kafkaHeatFeedSizeDto.getPageNumber(), 
            kafkaHeatFeedSizeDto.getPageSize()
        );
        log.info("Received the batch of {} subscribers.", followerIds.size());

        // creating the list of authors for each subscriber
        for (long userId : followerIds) {
            List<UserDto> followees = userServiceClient.getFollowing(userId);
            List<Long> followeeIds = followees.stream()
                .map(UserDto::id)
                .collect(Collectors.toList());
            followersIdsOfUser.put(userId, followeeIds);
        }

        // converting list of subscribers to a list of posts
        for (long subscriberId : followersIdsOfUser.keySet()) {
            List<Long> authorIds = followersIdsOfUser.get(subscriberId);
            authorIds.stream()
                .forEach(authorId -> followersPostIds.put(subscriberId, getListOfPostsIds(authorId)));
        }
    
        log.info("Starting saving to reeis feed for {} subscribers.", followersPostIds.size());
        for (long subscriberId : followersPostIds.keySet()) {
            List<Long> postIds = followersPostIds.get(subscriberId);
            postIds.stream()
                .forEach(postId -> recentPostService.addPostToUser(subscriberId, postId));
        }
    }

    private List<Long> getListOfPostsIds(long authorId) {
        return PostRepository.findByAuthorId(authorId).stream()
            .map(post -> post.getId())
            .collect(Collectors.toList());
    }
}
