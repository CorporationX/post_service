package faang.school.postservice.service.hashtag;

import faang.school.postservice.cache.service.PostCacheService;
import faang.school.postservice.dto.hashtag.HashtagViewDto;
import faang.school.postservice.dto.post.PostViewDto;
import faang.school.postservice.mapper.hashtag.HashtagMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static faang.school.postservice.util.HashtagUtils.getHashtags;

/**
 * HashtagServiceImpl — описание класса.
 * <p>
 * TODO: описать, какие обязанности у класса.
 * </p>
 *
 * @author Myrza
 * @since 08.08.2025
 */
@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final PostCacheService cacheService;
    private final HashtagMapper mapper;

    @Override
    @Transactional
    public void create(PostViewDto postDto) {
        var post = postRepository.findByIdOrThrow(postDto.id());
        var hashtagNames = getHashtags(post.getContent());
        var hashtags = new ArrayList<Hashtag>();
        for (String hashtagName : hashtagNames) {
            hashtags.add(getOrCreateHashtag(hashtagName));
            cacheService.addPost(hashtagName, postDto);
        }
        hashtagRepository.saveAll(hashtags);
    }

    private Hashtag getOrCreateHashtag(String name) {
        return hashtagRepository.findByName(name).orElseGet(() -> {
            var hashtag = new Hashtag();
            hashtag.setName(name);
            hashtag.setPosts(new HashSet<>());
            return hashtagRepository.save(hashtag);
        });
    }

    @Override
    @Transactional
    public void update(PostViewDto oldPostDto, PostViewDto newPostDto) {
        var post = postRepository.findByIdOrThrow(oldPostDto.id());
        var oldHashtags = post.getHashtags();
        var hasOldHashtags = oldHashtags != null;
        if (hasOldHashtags) {
            oldHashtags.forEach(hashtag -> cacheService.deletePost(hashtag.getName(), oldPostDto));
        }
        var hashtagNames = getHashtags(newPostDto.content());
        var newHashtags = new HashSet<Hashtag>();
        for (String hashtagName : hashtagNames) {
            var hashtag = getOrCreateHashtag(hashtagName);
            if (containHashtag(oldHashtags, hashtagName)) {
                hashtag.getPosts().remove(post);
            }
            newHashtags.add(hashtag);
            cacheService.addPost(hashtagName, newPostDto);
        }
        post.setHashtags(newHashtags);
        postRepository.save(post);
    }

    public boolean containHashtag(Set<Hashtag> hashtags, String target) {
        return hashtags != null && hashtags.stream()
                .anyMatch(hashtag -> hashtag.getName().equals(target));
    }

    @Override
    public void delete(PostViewDto postDto) {
        var hashtags = getHashtags(postDto.content());
        if (hashtags == null) {
            return;
        }
        for (String hashtag : hashtags) {
            cacheService.deletePost(hashtag, postDto);
        }
    }

    @Override
    public List<PostViewDto> getList(String hashtagName) {
        return cacheService.getList(hashtagName);
    }

    @Override
    public List<HashtagViewDto> getPopularHashtags(long offset, long limit) {
        var hashtagNames = cacheService.getPopularHashtags(offset, limit);
        var hashtags = hashtagRepository.findAllByNameIn(hashtagNames);
        return mapper.toViewDtoList(hashtags);
    }
}
