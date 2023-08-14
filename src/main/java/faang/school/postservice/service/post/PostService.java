package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "postsCache")
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Transactional(readOnly = true)
    public List<PostDto> getPostsByHashtagOrderByDate(String hashtag) {
        return postMapper.toListDto(postRepository.findByHashtagOrderByDate("#" + hashtag));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "hashtags", key = "#hashtag")
    public List<PostDto> getPostsByHashtagOrderByPopularity(String hashtag) {
        return postMapper.toListDto(postRepository.findByHashtagOrderByPopularity("#" + hashtag));
    }

    private void extractHashtagsWhileCreating(PostDto postDto) {
        List<String> hashtags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(postDto.getContent());

        while (matcher.find()) {
            hashtags.add(matcher.group());
        }

        postDto.setHashtags(hashtags);
    }
}