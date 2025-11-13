package faang.school.postservice.job.moderator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Comment;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Getter
@ConfigurationProperties
@RequiredArgsConstructor
@Component
public class ModerationDictionary {

    private static final String KEY_FOR_BAN_WORDS = "ban_words";

    private final ObjectMapper objectMapper;
    private final ClassPathResource resource;

    private List<String> banWords;

    @PostConstruct
    public void loadBanWords() {

        try (InputStream inputStream = resource.getInputStream()) {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            JsonNode banWordsNode = rootNode.get(KEY_FOR_BAN_WORDS);

            List<String> words = new ArrayList<>();
            if (banWordsNode != null && banWordsNode.isArray()) {
                banWordsNode.forEach(node -> words.add(node.asText()));
            }
            this.banWords = doToLowerCase(words);
        } catch (Exception e) {
            throw new RuntimeException("The file with prohibited words was not loaded: " + e.getMessage());
        }
    }

    public void verifyAndEditComment(Comment comment) {
        String text = comment.getContent();
        if (text == null) {
            comment.setIsVerified(true);
            return;
        }


        String originalText = text;
        String lowerText = text.toLowerCase();
        boolean isVerified = true;
        System.out.println(banWords);
        for (String banWord : banWords) {
            if (lowerText.contains(banWord)) {
                originalText = originalText.replaceAll("(?iu)" + Pattern.quote(banWord),
                        "*".repeat(banWord.length()));
                isVerified = false;
            }
        }

        comment.setContent(originalText);
        comment.setIsVerified(isVerified);
    }

    private List<String> doToLowerCase(List<String> list) {
        return list.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    public boolean isGoodString(String content) {
        if (content == null || content.isBlank()) {
            return true;
        }

        String contentLowerCase = content.toLowerCase();
        for (String banWord : banWords) {
            if (contentLowerCase.contains(banWord)) {
                return false;
            }
        }
        return true;
    }
}
