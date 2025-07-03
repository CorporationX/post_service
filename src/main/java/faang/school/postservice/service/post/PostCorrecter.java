package faang.school.postservice.service.post;

import faang.school.postservice.dto.languagetool.LanguageToolResponse;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.languagetool.LanguageToolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostCorrecter {
    private final LanguageToolService languageToolService;

    public void correctingBatchPosts(List<Post> posts) {
        posts.forEach(post -> {
            String content = post.getContent();
            LanguageToolResponse response = languageToolService.checkText(content);

            String correctedContent = applyCorrected(content, response);
            post.setCorrectedContent(correctedContent);
        });
    }

    private String applyCorrected(String originalText, LanguageToolResponse response) {
        List<LanguageToolResponse.Match> matches = response.getMatches();
        if (matches.isEmpty()) {
            return originalText;
        }

        StringBuilder text = new StringBuilder(originalText);
        matches.sort(Comparator.comparingInt(LanguageToolResponse.Match::getOffset).reversed());

        for (LanguageToolResponse.Match match : matches) {
            String replacement = match.getReplacements().get(0).getValue();
            int offset = match.getOffset();
            int length = match.getLength();

            text.replace(offset, offset + length, replacement);
        }
        String correctedText = text.toString();
        log.info("applying text adjustments. original: {}, corrected: {}", originalText, correctedText);
        return correctedText;
    }
}