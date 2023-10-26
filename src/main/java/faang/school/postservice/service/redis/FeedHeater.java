package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class FeedHeater {
    private final PostService postService;
    private final PostRepository postRepository;
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;
    private final RedisPostMapper redisPostMapper;

    public void heatUserFeed(UserDto userDto) {
        List<Long> followeeIds = userDto.getFollowers();
        List<PostDto> firstPostsForFeed = postService.getFirstPostsForFeed(followeeIds, postsFeedSize);
        firstPostsForFeed.forEach(postDto -> {
            if (!redisPostRepository.existsById(postDto.getId())) {
                redisPostRepository.save(redisPostMapper.toRedisPost(postDto));
            }
            getOrSaveRedisUser(postDto.getAuthorId());
        });
        if (redisFeedRepository.findById(userDto.getId()).isEmpty()) {
            List<TimePostId> list = firstPostsForFeed.stream().map(postDto -> TimePostId.builder()
                    .publishedAt(postDto.getPublishedAt())
                    .postId(postDto.getId())
                    .build()).toList();
            SortedSet<TimePostId> feed = new TreeSet<>(list);
            RedisFeed redisFeed = RedisFeed.builder().postIds(feed).userId(userDto.getId()).build();
            redisFeedRepository.save(redisFeed);
        }
    }
}
