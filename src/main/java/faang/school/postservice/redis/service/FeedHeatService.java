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

    public void sendHeatEvents(){
        var allUsers = userServiceClient.getAllUsers();
        authorCacheService.saveAllAuthorsInCache(allUsers);

        var feedEvents = generateFeedsForAllUserFollowers(allUsers);
        sendFeedHeatEvents(feedEvents);


        var postEvents = generatePostEvents(feedEvents);
        sendPostHeatEvents(postEvents);
    }

    private void sendPostHeatEvents(List<PostDto> postEvents) {
        postEvents.forEach(kafkaEventProducer::sendPostHeatEvent);
    }

    private void sendFeedHeatEvents(List<FeedDto> feedEvents) {
       feedEvents.forEach(kafkaEventProducer::sendFeedHeatEvent);
    }

    private List<FeedDto> generateFeedsForAllUserFollowers(List<UserDto> allUsersInOurSystem) {
        return allUsersInOurSystem.parallelStream()
                .map(follower -> {
                    var followerId = follower.getId();
                    var bloggers = userServiceClient.getUsersByIds(follower.getFollowees());

                    var allBloggersPostIds = bloggers.stream()
                            .flatMap(blogger -> blogger.getPosts().stream())
                            .limit(maxPostsInHeatFeed)
                            .toList();

                    var allBloggersPosts = postService.getPostsByIds(allBloggersPostIds);

                    return new FeedDto(followerId, allBloggersPosts);
                })
                .toList();
    }

    private List<PostDto> generatePostEvents(List<FeedDto> feedDtos){
        return feedDtos.stream()
                .flatMap(feedDto -> feedDto.posts().stream())
                .distinct()
                .toList();
    }
}