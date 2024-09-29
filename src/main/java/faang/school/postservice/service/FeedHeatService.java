package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.events.FeedDto;
import faang.school.postservice.kafka.producer.KafkaEventProducer;
import faang.school.postservice.redis.service.AuthorCacheService;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedHeatService {
    @Value("")//TODO add to app.yaml
    private int maxPostsInHeatFeed;

    private final KafkaEventProducer kafkaEventProducer;
    private final AuthorCacheService authorCacheService;
    private final UserServiceClient userServiceClient;

    private final PostService postService;

    public void sendHeatEvents(){
        //TODO save all authors tt we have in REDIS.
        var allUsers = userServiceClient.getAllUsers();
        var savedAuthorsInRedis = authorCacheService.saveAllAuthorsInRedis(allUsers);

        //TODO create Feeds for each author and send them to KAFKA
        var feedEvents = generateFeedEvents(allUsers);
        feedEvents
                .forEach(kafkaEventProducer::sendFeedEvent);

        //TODO save posts with comments and likes and send them to KAFKA.
        var postEvents = generatePostEvents(feedEvents);
        postEvents
                .forEach(kafkaEventProducer::sendPostEvent);
    }
    //TODO create class EventsGenerator
    private List<FeedDto> generateFeedEvents(List<UserDto> userDtos) {
        List<FeedDto> feedDtos = new ArrayList<>();

        for (UserDto userDto : userDtos) {
            var followerId = userDto.getId();
            var followeesUserDtos = userServiceClient.getUsersByIds(userDto.getFollowees());

            var postsOfFollowees = followeesUserDtos.stream()
                    .flatMap(followee -> followee.getPosts().stream())
                    .limit(maxPostsInHeatFeed)
                    .toList();

            var feedDto = new FeedDto(followerId, postsOfFollowees);
            feedDtos.add(feedDto);
        }

        return feedDtos;
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