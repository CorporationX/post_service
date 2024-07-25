package faang.school.postservice.service.cashe;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CacheService {
    private final RedisTemplate<String, Serializable> redisTemplate;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final HashtagRepository hashtagRepository;

    @Value("${spring.data.redis.cache.expiration:3600}")
    private long cacheExpiration;

    @Value("${spring.data.cache.size}")
    private int cacheSize;

    public void cachePostsByHashtag(String hashtag, List<Long> postIds) {
        redisTemplate.opsForValue().set("hashtag:" + hashtag, (Serializable) postIds, cacheExpiration,
                TimeUnit.SECONDS);
    }

    public void cachePost(PostDto postDto) {
        redisTemplate.opsForValue().set("post:" + postDto.getId(), postDto, cacheExpiration,
                TimeUnit.SECONDS);
    }

    @Transactional
    public void initializeCache() {
        List<Hashtag> popularHashtags = hashtagRepository.findPopularHashtags(PageRequest.of(0, cacheSize));
        for (Hashtag hashtag : popularHashtags) {
            List<Post> posts = postRepository.findByHashtagsNameOrderByCreatedAtDesc(hashtag.getName());

            List<Long> postIds = new ArrayList<>();
            for (Post post : posts) {
                PostDto postDto = postMapper.toDto(post);
                cachePost(postDto);
                postIds.add(post.getId());
            }

            cachePostsByHashtag(hashtag.getName(), postIds);
        }
    }
    public void clearCache() {
        log.info("Clearing all keys from Redis");
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}