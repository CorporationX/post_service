package faang.school.postservice.service.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.post.ForbiddenWordsLoadingException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
@Service
public class ModerationDictionary {
    private final ObjectMapper objectMapper;

    private Set<String> forbiddenWords;

    @PostConstruct
    public void loadForbiddenWords() {
        try {
            forbiddenWords = new HashSet<>(objectMapper.readValue(
                    new ClassPathResource("forbidden-words.json").getInputStream(),
                    Set.class
            ));
        } catch (IOException e) {
            throw new ForbiddenWordsLoadingException();
        }
    }

    public boolean containsForbiddenWord(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }

        String lowerCaseContent = content.toLowerCase();

        return forbiddenWords.stream()
                .anyMatch(lowerCaseContent::contains);
    }
}