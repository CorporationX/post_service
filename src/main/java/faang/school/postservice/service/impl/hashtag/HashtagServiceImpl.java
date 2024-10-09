package faang.school.postservice.service.impl.hashtag;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.hashtag.HashtagRepository;
import faang.school.postservice.service.HashtagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    private static final long DAYS_TO_LIVE_IN_REDIS = 1;
    private final HashtagRepository hashtagRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostMapper postMapper;

    @Override
    @Transactional
    @Async
    public void createHashtags(Post post) {
        List<Hashtag> postHashtags = processHashtags(post);

        post.setHashtags(postHashtags);

        hashtagRepository.saveAll(postHashtags);
    }

    @Override
    @Transactional
    public void updateHashtags(Post post) {
        List<Hashtag> postHashtags = processHashtags(post);

        List<String> hashtagsToDelete = post.getHashtags().stream()
                .filter(hashtag -> !postHashtags.contains(hashtag))
                .map(Hashtag::getName)
                .toList();

        updateCacheRedis(post, hashtagsToDelete);

        post.getHashtags().removeIf(hashtag -> hashtagsToDelete.contains(hashtag.getName()));

        postHashtags.forEach(hashtag -> {
            if (!post.getHashtags().contains(hashtag)) {
                post.getHashtags().add(hashtag);
//                hashtag.getPosts().add(post);
            }
        });

        hashtagRepository.saveAll(postHashtags);
    }

    private void updateCacheRedis(Post post, List<String> hashtagsToDelete) {
        if (!hashtagsToDelete.isEmpty()) {
            hashtagsToDelete.forEach(hashtag -> {
                if (Boolean.TRUE.equals(redisTemplate.hasKey(hashtag))) {
                    List<PostDto> cachedPosts = (List<PostDto>) redisTemplate.opsForValue().get(hashtag);

                    if (cachedPosts != null) {
                        cachedPosts.removeIf(cachePost -> cachePost.id() == post.getId());

                        if (cachedPosts.isEmpty()) {
                            redisTemplate.delete(hashtag);
                        } else {
                            redisTemplate.opsForValue().set(hashtag, cachedPosts);
                            redisTemplate.expire(hashtag, DAYS_TO_LIVE_IN_REDIS, TimeUnit.DAYS);
                        }
                    }
                }
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostDto> findPostsByHashtag(String hashtag) {

        if (Boolean.TRUE.equals(redisTemplate.hasKey(hashtag))) {
            return (List<PostDto>) redisTemplate.opsForValue().get(hashtag);
        }

        List<Post> posts = hashtagRepository
                .findByName(hashtag)
                .map(Hashtag::getPosts)
                .orElseGet(ArrayList::new);

        List<PostDto> postsDto = postMapper.toDto(posts);
        postsDto.sort(Comparator.comparing(PostDto::publishedAt).reversed());

        redisTemplate.opsForValue().set(hashtag, postsDto);
        redisTemplate.expire(hashtag, DAYS_TO_LIVE_IN_REDIS, TimeUnit.DAYS);

        return postsDto;
    }

    @Transactional(readOnly = true)
    private List<Hashtag> processHashtags(Post post) {
        List<String> foundHashtags = findHashtags(post.getContent());

        return foundHashtags.stream()
                .map(tag -> hashtagRepository.findByName(tag)
                        .orElseGet(() -> Hashtag.builder().name(tag).build()))
                .toList();
    }

    private List<String> findHashtags(String content) {
        List<String> foundHashtags = new ArrayList<>();

        if (content == null || content.isBlank()) {
            return foundHashtags;
        }

        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            foundHashtags.add(matcher.group().substring(1));
        }

        return foundHashtags;
    }
}
