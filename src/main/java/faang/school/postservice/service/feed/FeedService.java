package faang.school.postservice.service.feed;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.redis.PostDtoRedis;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.entity.redis.Feed;
import faang.school.postservice.entity.redis.Posts;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<PostFeedDto> getPostsBuUserid(long userId, long capacity){
        Feed feed = redisFeedRepository.findById(userId).orElse(null);
        log.info("feed in redis ----> {}", feed);

        Set<String> postsfindAll = redisTemplate.keys("posts:*");
        log.info("keys ----> {}", postsfindAll);

        if (feed == null || feed.getPosts().isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> postsId = feed.getPosts().stream().limit(capacity).toList();
//        feed.getPosts().removeAll(postsId);
        log.info("postsId in feed ----> {}", postsId);

        List<Posts> posts = new ArrayList<>();
        for (Long aLong : postsId) {
//            PostDtoRedis postDto = (PostDtoRedis) redisTemplate.opsForHash().get("posts:" + aLong, "postDto");
            Map<Object, Object> posts1 = redisTemplate.opsForHash().entries("posts:" + aLong);
            log.info("posts1 - {}", posts1);
//            posts.add(posts1);
        }

//        Iterable<Posts> postsIterable = redisPostRepository.findAllById(postsId);
//        log.info("postsIterable in feed ----> {}", postsIterable);
//        List<Posts> posts = new ArrayList<>();
//        postsIterable.forEach(posts::add);

        List<PostDtoRedis> postDtoRedis = new ArrayList<>();
//        posts.forEach(i -> postDtoRedis.add(i.getPostDto()));
//        List<PostDtoRedis> postDtoRedis = StreamSupport
//                .stream(((Iterable<Posts>) redisPostRepository.findAllById(postsId)).spliterator(), false)
//                .filter(Objects::nonNull)
//                .map(Posts::getPostDto)
//                .toList();

        List<PostFeedDto> postFeedDtos = new ArrayList<>();
        for (PostDtoRedis postDto : postDtoRedis) {
            UserDto userDto = redisUserRepository.findById(postDto.getAuthorId()).orElse(null).getUserDto();
            postFeedDtos.add(PostFeedDto.builder()
                    .userDto(userDto)
                    .postDtoRedis(postDto)
                    .build());
        }
        return postFeedDtos;


//        List<Long> authorsId = postDtoRedis.stream().map(i -> i.getPostDto().getAuthorId()).toList();

    }
}
