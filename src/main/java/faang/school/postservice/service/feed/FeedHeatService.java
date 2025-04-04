package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class FeedHeatService {

    @Value("${spring.data.redis.heat.max-posts-in-feed}")
    private int maxPostsInHeatFeed;

    private final KafkaEventProducer kafkaEventProducer;
    private final AuthorCacheService authorCacheService;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public void sendHeatEvents() {
        List<UserDto> allUsers = userServiceClient.getAllUsers();
        authorCacheService.saveAllAuthorsInCache(allUsers).join();

        List<FeedDto> feedEvents = generateFeedsForAllUserFollowers(allUsers);
        sendFeedHeatEvents(feedEvents);

        List<PostDto> postEvents = generatePostEvents(feedEvents);
        sendPostHeatEvents(postEvents);
    }

    private void sendPostHeatEvents(List<PostDto> postEvents) {

        CompletableFuture.allOf(postEvents.stream()
                .map(postDto -> kafkaEventProducer.sendPostHeatEvent(postDto)
                        .exceptionally(ex -> {
                            log.error("Failed to send PostHeatEvent for postId={}", postDto.getId(), ex);
                            return null;
                        })).toArray(CompletableFuture[]::new)).join();
        log.info("All PostHeatEvents sent.");
    }

    private void sendFeedHeatEvents(List<FeedDto> feedEvents) {

        CompletableFuture.allOf(feedEvents.stream()
                .map(feedDto -> kafkaEventProducer.sendFeedHeatEvent(feedDto)
                        .exceptionally(ex -> {
                            log.error("Failed to send FeedHeatEvent for followerId={}", feedDto.getFollowerId(), ex);
                            return null;
                        })).toArray(CompletableFuture[]::new)).join();
        log.info("All FeedHeatEvents sent.");
    }

    private List<FeedDto> generateFeedsForAllUserFollowers(List<UserDto> allUsersInOurSystem) {
        return allUsersInOurSystem.stream()
                .map(follower -> {
                    Long followerId = follower.getId();
                    List<Long> followees = follower.getFollowees() != null ? follower.getFollowees() : List.of();

                    if (followees.isEmpty()) {
                        log.info("User {} has no followees. Empty feed will be generated.", followerId);
                    }

                    List<UserDto> bloggers = userServiceClient.getUsersByIds(followees);

                    List<Long> allBloggersPostIds = bloggers.stream()
                            .flatMap(blogger -> blogger.getPosts().stream())
                            .limit(maxPostsInHeatFeed)
                            .collect(Collectors.toList());

                    List<PostDto> allBloggersPosts = postService.getPostsByIds(allBloggersPostIds);

                    return new FeedDto(followerId, allBloggersPosts);
                })
                .collect(Collectors.toList());
    }

    private List<PostDto> generatePostEvents(List<FeedDto> feedDtos) {
        return feedDtos.stream()
                .flatMap(feedDto -> feedDto.getPosts().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}