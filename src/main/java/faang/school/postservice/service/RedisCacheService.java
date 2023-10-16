package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.TimePostId;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisCacheService {

    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserRepository redisUserRepository;
    private final RedisUserMapper redisUserMapper;
    private final RedisFeedRepository redisFeedRepository;
    private final UserServiceClient userServiceClient;
    private final KafkaProducer kafkaProducer;

    public void putPostAndAuthorInCache(Post post) {
        UserDto user = userServiceClient.getUser(post.getAuthorId());
        redisUserRepository.save(redisUserMapper.toEntity(user));
        RedisPost redisPost = redisPostRepository.save(redisPostMapper.toRedisPost(post));
        TimePostId timePostId = TimePostId.builder()
                .id(post.getId())
                .publishedAt(post.getPublishedAt())
                .build();
        kafkaProducer.sendNewPostInFeed(user.getFollowerIds(), timePostId);
    }

    public void saveFeedInCache(KafkaPostDto kafkaPostDto) {
        Optional<RedisFeed> optional = redisFeedRepository.findById(kafkaPostDto.getUserId());
        if (optional.isPresent()) {
            RedisFeed redisFeed = optional.get();
            redisFeed.getPostIds().add(kafkaPostDto.getPost());
            //optimisticLock
            redisFeedRepository.save(redisFeed);
        } else {
            SortedSet<TimePostId> postIds = new TreeSet<>();
            postIds.add(kafkaPostDto.getPost());
            RedisFeed newFeed = RedisFeed.builder()
                    .userId(kafkaPostDto.getUserId())
                    .postIds(postIds)
                    .build();
        }
    }

    public void deletePostFromCache(Post post) {
        redisPostRepository.delete(redisPostMapper.toRedisPost(post));
    }

    public RedisPost updatePostInCache(Post post) {
        Optional<RedisPost> redisPost = redisPostRepository.findById(post.getId());
        return null;
    }
}
