package faang.school.postservice.service.impl;

import faang.school.postservice.client.CorrecterTextClient;
import faang.school.postservice.dto.text.gears.TextGearsResponse;
import faang.school.postservice.exception.TextGearsException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostCorrecterService;
import faang.school.postservice.validator.TextGearsValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentCorrecterServiceImpl implements PostCorrecterService {

    private final PostRepository postRepository;
    private final CorrecterTextClient<TextGearsResponse> correcterTextClient;
    private final TextGearsValidator textGearsValidator;
    private final ApplicationContext applicationContext;

    @Override
    public void correctAllPosts() {
        List<Post> posts = postRepository.findNotPublishedAndNotDeletedPosts();
        posts.forEach(post -> {
            try {
                ContentCorrecterServiceImpl proxy = applicationContext.getBean(ContentCorrecterServiceImpl.class);
                String correctContent = proxy.correctContent(post.getContent());
                post.setContent(correctContent);
            } catch (TextGearsException e) {
                log.error("One of the posts is not subject to processing! But for other posts, processing continues...");
            }
        });
        postRepository.saveAll(posts);
    }

    @Retryable(maxAttemptsExpression = "${post.correct.content.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${post.correct.content.retry.delay}"),
            retryFor = TextGearsException.class)
    public String correctContent(String content) {
        TextGearsResponse response = correcterTextClient.correctText(content);
        textGearsValidator.isCorrectResponse(response);
        return response.getResponse().getCorrected();
    }
}
