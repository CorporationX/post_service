package faang.school.postservice.service.cashe;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final RedisTemplate<String, Serializable> redisTemplate;
    private final PostRepository postRepository;
    private final HashtagRepository hashtagRepository;

    @Value("${spring.data.redis.cache.expiration:3600}")
    private long cacheExpiration;

    public void cachePostsByHashtag(String hashtag, List<Post> posts) {
        redisTemplate.opsForValue().set(hashtag, (Serializable) posts, cacheExpiration, TimeUnit.SECONDS);
    }

    @Transactional
    public void initializeCache() {
        List<Hashtag> popularHashtags = hashtagRepository.findPopularHashtags(PageRequest.of(0, 100));
        for (Hashtag hashtag : popularHashtags) {
            List<Post> posts = postRepository.findByHashtagsNameOrderByCreatedAtDesc(hashtag.getName());
            posts.forEach(this::initializeLazyCollections);
            cachePostsByHashtag(hashtag.getName(), posts);
        }
    }

    @Scheduled(fixedRateString = "${cache.refresh.rate:3600000}")
    @Transactional
    public void refreshCache() {
        initializeCache();
    }

    private void initializeLazyCollections(Post post) {
        if (post.getLikes() != null) {
            post.getLikes().size();
        }
        if (post.getComments() != null) {
            post.getComments().size();
        }
        if (post.getResources() != null) {
            post.getResources().size();
        }
        if (post.getAlbums() != null) {
            post.getAlbums().size();
        }
        if (post.getAd() != null) {
            post.getAd().getId();
        }
    }
}