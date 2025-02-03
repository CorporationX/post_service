package faang.school.postservice.listener;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.rediscache.FeedRedisRepository;
import faang.school.postservice.repository.rediscache.PostRedisRepository;
import faang.school.postservice.repository.rediscache.UserRedisRepository;
import faang.school.postservice.utils.PublishedPostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class NewPostListenerKafka {
    private final PostRedisRepository postRedisRepository;
    private final FeedRedisRepository feedRedisRepository;
    private final UserRedisRepository userRedisRepository;

    @KafkaListener(groupId = "posts-group", topics = "posts",
                   containerFactory = "postsKafkaListenerContainerFactory",
                   concurrency = "3")
    public void listener(PublishedPostMessage post) {
        log.info("Received message [{}] in posts-group", post);
        cacheToPostRedis(post.getPost());
        log.info("Post with ID: {} was cached to Redis", post.getPost().getId().toString());

        cacheToUserRedis(post.getUserDto());
        log.info("User with ID: {} was cached to Redis", post.getUserDto().getId().toString());

        cacheToFeedRedis(post);
        log.info("Post with ID: {} was cached to Redis", post.getPost().getId().toString());
    }

    private void cacheToPostRedis(Post post) {
        postRedisRepository.save(post.getId().toString(), post);
    }

    private void cacheToUserRedis(UserDto userDto) {
        userRedisRepository.save(userDto.getId().toString(), userDto);
    }

    private void cacheToFeedRedis(PublishedPostMessage post) {
        List<Long> userFollowers = post.getUserDto().getUserFollowerIds();
        userFollowers.parallelStream().forEach(followerId ->
                feedRedisRepository.save(followerId.toString(), post.getPost().toString()));
    }
}
