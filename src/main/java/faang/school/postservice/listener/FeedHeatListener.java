package faang.school.postservice.listener;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.rediscache.FeedRedisRepository;
import faang.school.postservice.repository.rediscache.PostRedisRepository;
import faang.school.postservice.repository.rediscache.UserRedisRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.utils.PublishedPostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedHeatListener {

    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final PostMapper postMapper;
    private final PostRedisRepository postRedisRepository;
    private final FeedRedisRepository feedRedisRepository;
    private final UserRedisRepository userRedisRepository;

    @KafkaListener(topics = "feed-heat-topic", groupId = "feed-heat-group")
    public void processHeatTask(String userId) {
        log.info("Received message [{}] in feed-heat-group", userId);
        UserDto user = userServiceClient.getUser(Long.parseLong(userId));
        List<Post> usersAllPosts = postService.getAllPublishedPostsByUserId(user.getId()).stream()
                .map(postMapper::toEntity)
                .toList();

        for (Post post : usersAllPosts) {
            cacheToPostRedis(post);
            PublishedPostMessage publishedPostMessage = new PublishedPostMessage(post, user);
            cacheToFeedRedis(publishedPostMessage);
        }

        cacheToUserRedis(user);
    }

    private void cacheToPostRedis(Post post) {
        log.info("Saving post with ID: {} to Redis", post.getId());
        postRedisRepository.save(post.getId().toString(), post);
    }

    private void cacheToUserRedis(UserDto userDto) {
        log.info("Saving user with ID: {} to Redis", userDto.getId());
        userRedisRepository.save(userDto.getId().toString(), userDto);
    }

    private void cacheToFeedRedis(PublishedPostMessage post) {
        log.info("Saving post with ID: {} to Feed for User {}", post.getPost().getId(),
                post.getUserDto().getId());
        List<Long> userFollowers = post.getUserDto().getUserFollowerIds();
        userFollowers.parallelStream().forEach(followerId ->
                feedRedisRepository.save(followerId.toString(), post.getPost().toString()));
    }
}
