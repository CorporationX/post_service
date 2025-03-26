package faang.school.postservice.service.hashtags;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.mapper.HashtagsPostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.validationUtils.HashtagValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashtagService {
    @Value("${app.hashtags.max-cached-posts-per-hashtag}")
    private int maxCachedPosts;

    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;
    private final HashtagRedisService hashtagRedisService;
    private final HashtagsPostMapper postMapper;

    public Page<PostResponseDto> getPostsByHashtag(HashtagRequestDto hashtagRequestDto) {
        HashtagValidation.validateHashtagRequestDto(hashtagRequestDto);
        int page = hashtagRequestDto.getPage();
        int size = hashtagRequestDto.getSize();
        Page<PostResponseDto> postPageFromRedis = null;
        if (page + size <= maxCachedPosts) {
            postPageFromRedis = hashtagRedisService.getPostsByHashtag(hashtagRequestDto);
        }
        return postPageFromRedis != null ? postPageFromRedis : getPostsFromDB(hashtagRequestDto);
    }

    public void extractHashtagsFromContent(Post post) {
        Pattern pattern = Pattern.compile("#(\\S+)");
        Matcher matcher = pattern.matcher(post.getContent());
        Set<Hashtag> hashtags = new HashSet<>();
        while (matcher.find()) {
            String tag = matcher.group().substring(1);
            Hashtag foundHashtag = hashtagRepository.findByTag(tag);
            if (foundHashtag == null) {
                foundHashtag = hashtagRepository.save(Hashtag.builder().tag(tag).count(0L).build());
            } else {
                foundHashtag.setCount(foundHashtag.getCount() + 1);
            }
            hashtags.add(foundHashtag);
            hashtagRedisService.saveHashtag(tag, post);
        }
        post.setHashtags(hashtags);
        postRepository.save(post);
    }

    private Page<PostResponseDto> getPostsFromDB(HashtagRequestDto hashtagRequestDto) {
        int page = hashtagRequestDto.getPage();
        int size = hashtagRequestDto.getSize();
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postRepository.findPostsByHashtag(pageable, hashtagRequestDto.getTag());
        return postPage.map(postMapper::toPostResponseDto);
    }
}
