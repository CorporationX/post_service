package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    public TreeSet<PostFeedDto> getNewsFeed(Long postId, long userId){

    }
}
