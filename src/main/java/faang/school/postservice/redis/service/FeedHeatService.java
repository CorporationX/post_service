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

@Service
@RequiredArgsConstructor
public class FeedHeatService {
    @Value("spring.data.redis.heat.max-posts-in-feed:500")
    private int maxPostsInHeatFeed;

    private final KafkaEventProducer kafkaEventProducer;
    private final AuthorCacheService authorCacheService;
    private final UserServiceClient userServiceClient;

    private final PostService postService;
    //TODO need several threads
    public void sendHeatEvents(){

        var allUsers = userServiceClient.getAllUsers();
        authorCacheService.saveAllAuthorsInCache(allUsers);

        var feedEvents = generateFeedsForAllUserFollowers(allUsers);
        feedEvents
                .forEach(kafkaEventProducer::sendFeedHeatEvent);

        var postEvents = generatePostEvents(feedEvents);
        postEvents
                .forEach(kafkaEventProducer::sendPostHeatEvent);
    }

    private List<FeedDto> generateFeedsForAllUserFollowers(List<UserDto> allUsersInOurSystem) {
        List<FeedDto> feeds = new ArrayList<>();

        for (UserDto user : allUsersInOurSystem) {
            var followerId = user.getId();
            var userBloggers = userServiceClient.getUsersByIds(user.getFollowees());

            var allPostsOfUserBloggers = userBloggers.stream()
                    .flatMap(blogger -> blogger.getPosts().stream())
                    .limit(maxPostsInHeatFeed)
                    .toList();

            feeds.add(new FeedDto(followerId, allPostsOfUserBloggers));
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