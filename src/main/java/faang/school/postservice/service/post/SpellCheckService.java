package faang.school.postservice.service.post;

import faang.school.postservice.dto.corrector.CorrectWordDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpellCheckService {
    private final RestTemplate restTemplate;
    private final PostRepository postRepository;
    private final PostService postService;
    @Value("${spell-check.url}")
    private String url;

    public void spellCheckTextInPosts() {
        List<Post> posts = postService.findReadyToPublishAndUncorrected();
        if(!posts.isEmpty()) {
            posts.forEach(post -> {
                post.setContent(correctSpelling(post.getContent()));
                post.setCorrected(true);
            });
            postRepository.saveAll(posts);
        }
    }

    public void spellCheckPostById(Long postId) {
        Post post = postService.findPostById(postId);
        if(!post.isCorrected()) {
            post.setContent(correctSpelling(post.getContent()));
            post.setCorrected(true);
            postRepository.save(post);
        }
    }

    @Retryable
    private String correctSpelling(String content) {
        CorrectWordDto[] correctWordDto = request(content);
        if (correctWordDto != null) {
            return correctText(correctWordDto, content);
        }
        return content;
    }

    private String preparingUrl(String text) {
        return UriComponentsBuilder
                .fromHttpUrl(url)
                .queryParam("text", text)
                .build(false)
                .toUriString();

    }

    private String correctText(CorrectWordDto[] listCorrectWord, String text) {
        StringBuilder textPost = new StringBuilder(text);
        StringBuilder correctText = new StringBuilder();
        int lastPos = 0;
        for (CorrectWordDto correctWordDto : listCorrectWord) {
            int pos = correctWordDto.getPos();
            String word = correctWordDto.getSuggestions().get(0);
            correctText.append(textPost.substring(lastPos, pos));
            correctText.append(word);
            lastPos = pos + correctWordDto.getLen();
        }
        correctText.append(textPost.substring(lastPos));
        return correctText.toString();
    }

    @Retryable(retryFor = {ResourceAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    private CorrectWordDto[] request(String text) {
        String url = preparingUrl(text);
        return restTemplate.getForObject(url, CorrectWordDto[].class);
    }
}
