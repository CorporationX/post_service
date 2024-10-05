package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.events.FeedDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.*;

@Service
@RequiredArgsConstructor
public class FeedHeatService {
    @Value("spring.data.redis.heat.max-posts-in-feed:500")
    private int maxPostsInHeatFeed;

    private final KafkaEventProducer kafkaEventProducer;
    private final AuthorCacheService authorCacheService;
    private final UserServiceClient userServiceClient;
    private final PostService postService;

    public CompletableFuture<Void> sendHeatEvents(){
        var allUsers = userServiceClient.getAllUsers();
        var savedAllAuthorsInCache = authorCacheService.saveAllAuthorsInCache(allUsers);

        var feedEvents = generateFeedsForAllUserFollowers(allUsers);
        var feedEventFutures = sendFeedHeatEvents(feedEvents);


        var postEvents = generatePostEvents(feedEvents);
        var postEventFutures = sendPostHeatEvents(postEvents);

        return allOf(savedAllAuthorsInCache, feedEventFutures, postEventFutures);
    }

    private CompletableFuture<Void> sendPostHeatEvents(List<PostDto> postEvents) {
        var postEventFutures = postEvents.stream()
                .map(kafkaEventProducer::sendPostHeatEvent)
                .toArray(CompletableFuture[]::new);
        return allOf(postEventFutures);
    }

    private CompletableFuture<Void> sendFeedHeatEvents(List<FeedDto> feedEvents) {
        var feedEventFutures = feedEvents.stream()
                .map(kafkaEventProducer::sendFeedHeatEvent)
                .toArray(CompletableFuture[]::new);
        return allOf(feedEventFutures);
    }

    private List<FeedDto> generateFeedsForAllUserFollowers(List<UserDto> allUsersInOurSystem) {
        List<FeedDto> feeds = new ArrayList<>();

        for (UserDto follower : allUsersInOurSystem) {
            var followerId = follower.getId();
            var bloggers = userServiceClient.getUsersByIds(follower.getFollowees());

            var allBloggersPosts = bloggers.stream()
                    .flatMap(blogger -> blogger.getPosts().stream())
                    .limit(maxPostsInHeatFeed)
                    .toList();

            feeds.add(new FeedDto(followerId, allBloggersPosts));
        }
        return feeds;
    }

    private List<PostDto> generatePostEvents(List<FeedDto> feedDtos){
        var postIds = feedDtos.stream()
                .flatMap(feedDto -> feedDto.posts().stream())
                .distinct()
                .toList();

        return postService.getPostsByIds(postIds)
                .stream()
                .toList();
    }
}