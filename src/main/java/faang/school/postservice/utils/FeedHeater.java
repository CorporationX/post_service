package faang.school.postservice.utils;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.dto.redis.UserRedis;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.feed.FeedRedisRepository;
import faang.school.postservice.repository.post.PostRedisRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.repository.user.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Component
@RequiredArgsConstructor
public class FeedHeater {

    private final ExecutorService executorService;
    private final UserRedisRepository userRedisRepository;
    private final PostRepository postRepository;
    private final PostRedisRepository postRedisRepository;
    private final FeedRedisRepository feedRedisRepository;
    private final UserServiceClient userServiceClient;

    public void heat() {
        executorService.execute(() -> {
            List<Post> postList = postRepository.findAll();
            postList.forEach(post -> {
                UserDto userDto = userServiceClient.getUser(post.getAuthorId());
                userDto.getFollowersIds().forEach(id -> {
                    feedRedisRepository.addPostToFeed(userDto.getId(), post.getId(),
                            post.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                });
                userRedisRepository.save(UserRedis.builder()
                        .id(userDto.getId())
                        .email(userDto.getEmail())
                        .followers(userDto.getFollowersIds())
                        .username(userDto.getUsername())
                        .build());
                postRedisRepository.save(PostRedis.builder()
                                .postId(post.getId())
                                .countLikes((long) post.getLikes().size())
                                .authorId(post.getAuthorId())
                                .countComments((long) post.getComments().size())
                        .build());
            });
        });
    }
}
