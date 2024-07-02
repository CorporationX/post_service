package faang.school.postservice.service.post.corrector;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.corrector.GrammarBotProperities;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrector {
    private static final String POST = "POST";
    private static final String LANG = "en";
    @Value("${corrector.request-delay}")
    private long requestDelay;
    private final GrammarBotProperities correctorProperties;
    private final ObjectMapper objectMapper;
    private final PostService postService;


    @Scheduled(cron = "${post.corrector.scheduler.cron}")
    public void correctPosts() {
        List<Post> postsToBeCorrected = postService.getAllDrafts();

        postsToBeCorrected.forEach(post -> post.setContent(correctPostContent(post.getContent())));

        postService.updatePosts(postsToBeCorrected);
    }

    private String correctPostContent(String postContent) {
        Content contentToBeCorrected = Content.builder()
                .text(postContent)
                .lang(LANG)
                .build();
        try {
            CorrectorResponse correctorResponse = getCorrectorResponse(contentToBeCorrected);
            return correctorResponse.getCorrection();
        } catch (IOException | InterruptedException | ExhaustedRetryException e) {
            log.error("Spell checking request failed: {}", e.getMessage());
        }
        return contentToBeCorrected.getText();
    }

    @Retryable
    private CorrectorResponse getCorrectorResponse(Content contentToBeCorrected) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(correctorProperties.getSpellCheckerUri()))
                .header(correctorProperties.getKeyHeader(), correctorProperties.getKeyValue())
                .header(correctorProperties.getContentTypeHeader(), correctorProperties.getContentTypeValue())
                .method(POST, HttpRequest.BodyPublishers.ofString(contentToBeCorrected.toString()))
                .build();

        Thread.sleep(requestDelay);
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), CorrectorResponse.class);
    }
}
