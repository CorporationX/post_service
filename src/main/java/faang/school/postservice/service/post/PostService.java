package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "postsCache")
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final HashtagRepository hashtagRepository;

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByHashtagOrderByDate(String hashtag) {
        return postMapper.toListDto(postRepository.findByHashtagOrderByDate(hashtag));
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "popularPostsByHashtag", condition = "@postService.isPopular(#hashtag)")
    public List<PostDto> getPostsByHashtagOrderByPopularity(String hashtag) {
        return postMapper.toListDto(postRepository.findByHashtagOrderByPopularity(hashtag));
    }

    boolean isPopular(String hashtag) {
        return hashtagRepository.getTop10Popular().stream()
                .map(Hashtag::getHashtag)
                .anyMatch(tag -> tag.equals(hashtag));
    }
}
