package faang.school.postservice.redis.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.producer.KafkaEventProducer;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event.FeedDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedHeatService {
    private final RedisProperties redisProperties;
    private final KafkaEventProducer kafkaEventProducer;
    private final AuthorCacheService authorCacheService;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public void sendHeatEvents() {
        List<UserDto> allUsers = userServiceClient.getAllUsers();
        authorCacheService.saveAllAuthorsInCache(allUsers);

        List<FeedDto> feedEvents = generateFeedsForAllUserFollowers(allUsers);
        sendFeedHeatEvents(feedEvents);

        List<PostDto> postEvents = generatePostEvents(feedEvents);
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
                .map(this::getFeed)
                .toList();
    }

    private FeedDto getFeed(UserDto follower) {
        Long followerId = follower.getId();
        List<UserDto> usersByIds = userServiceClient.getUsersByIds(follower.getFollowingsIds());
        Integer maxPostsInFeed = redisProperties.getMaxPostsInHeatFeed();

        List<Long> allUsersPostIds = usersByIds.stream()
                .flatMap(user -> user.getPosts().stream())
                .limit(maxPostsInFeed)
                .toList();

        List<PostDto> allUsersPost = postMapper.toDto(postRepository.findAllById(allUsersPostIds));

        return new FeedDto(followerId, allUsersPost);
    }

    private List<PostDto> generatePostEvents(List<FeedDto> feedDtos) {
        return feedDtos.stream()
                .flatMap(feedDto -> feedDto.posts().stream())
                .distinct()
                .toList();
    }
}