package faang.school.postservice.service.hashtags;

import faang.school.postservice.dto.hashtag.HashtagRequestDto;
import faang.school.postservice.dto.hashtag.PostResponseDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
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
    private final PostMapper postMapper;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public Page<PostResponseDto> getPostsByHashtag(HashtagRequestDto hashtagRequestDto) {
        int page = hashtagRequestDto.getPage();
        int size = hashtagRequestDto.getSize();
        if (page + size - 1 <= maxCachedPosts) {
            Page<PostResponseDto> postPageFromRedis = hashtagRedisService.getPostsByHashtag(hashtagRequestDto);
            return postPageFromRedis != null ? postPageFromRedis : getPostsFromDB(hashtagRequestDto);
        }
        return getPostsFromDB(hashtagRequestDto);
    }

    public void extractHashtagsFromContent(Post post) {
        Pattern pattern = Pattern.compile("#(\\S+)");
        Matcher matcher = pattern.matcher(post.getContent());
        Set<Hashtag> hashtags = new HashSet<>();
        while (matcher.find()) {
            String tag = matcher.group().substring(1);
            Hashtag foundHashtag = hashtagRepository.findByTag(tag);
            hashtags.add(Objects.requireNonNullElseGet(foundHashtag,
                    () -> hashtagRepository.save(Hashtag.builder().tag(tag).build())));
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

    public void test() {
        Post post = Post.builder()
                .content("YO #java # #codeWithMe234 #programming #redis #.")
                .projectId(1L)
                .published(true)
                .publishedAt(LocalDateTime.now())
                .build();

        postRepository.save(post);
        extractHashtagsFromContent(post);
    }
}
