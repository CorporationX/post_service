package faang.school.postservice.api;

import faang.school.postservice.api.client.CorrectorClient;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCorrector {
    private final CorrectorClient correctorClient;

    public String correctPost(Post post) {
        String language = correctorClient.getContentLanguageDialect(post.getContent());
        String result;
        if (language.startsWith("en")) {
            result = correctorClient.getAutoCorrectedEnglishText(post.getContent(), language);
        } else {
            result = correctorClient.getCorrectedNonEnglishText(post.getContent(), language);
        }
        log.debug("Post with id {} corrected", post.getId());
        return result;
    }
}
