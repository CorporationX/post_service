package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrecter {
    private final PostService postService;
    private final RestTemplate restTemplate;

    @Value("${spellchecker.api.url}")
    private String URL;

    private final PostRepository postRepository;

    @Retryable(
            retryFor = {RuntimeException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void correctingSpellingPost(Post post) {
        String content = post.getContent();
        try {
            String correctedContent = restTemplate.getForObject(URL, String.class, content);
            post.setContent(correctedContent);
        } catch (RestClientException e) {
            log.error("ошибка");
            throw new RuntimeException();
        }
    }

    public List<Post> correctingSpellingOfPosts() {
        List<Post> posts = postService.getAllUnpublishedPost();
        posts.forEach(this::correctingSpellingPost);

        return posts;

    }


}
