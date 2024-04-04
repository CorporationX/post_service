package faang.school.postservice.corrector;

import faang.school.postservice.dto.corrector.CorrectWordDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ContentCorrector {
    private final RestTemplate restTemplate;
    private final PostRepository postRepository;
    @Value("${spell-check.url}")
    private String url;

    public void spellCheckTextInPosts() {
        List<Post> posts = getReadyToPublishPost();
        if(posts != null) {
            posts.forEach(post -> {
                post.setContent(correctSpelling(post.getContent()));
            });
            postRepository.saveAll(posts);
        }
    }

    public void spellCheckPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with id %s not found", postId)));
        post.setContent(correctSpelling(post.getContent()));
        postRepository.save(post);
    }

    @Retryable
    private String correctSpelling(String content) {
        CorrectWordDto[] correctWordDto = request(content);
        if (correctWordDto != null) {
            return correctionText(correctWordDto, content);
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

    private String correctionText(CorrectWordDto[] listCorrectWord, String text) {
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

    private List<Post> getReadyToPublishPost() {
        return postRepository.findReadyToPublish();
    }

    @Retryable(retryFor = {ResourceAccessException.class}, maxAttempts = 3, backoff = @Backoff(delay = 3000))
    private CorrectWordDto[] request(String text) {
        String url = preparingUrl(text);
        return restTemplate.getForObject(url, CorrectWordDto[].class);
    }
}
