package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.ForbiddenWordsFileNotFoundException;
import faang.school.postservice.exception.post.ForbiddenWordsLoadingException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Service
public class ModerationDictionary {
    private Set<String> forbiddenWords;

    @PostConstruct
    public void loadForbiddenWords() {
        try (InputStream inputStream = getClass().getResourceAsStream("/forbidden-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            if (inputStream == null) {
                throw new ForbiddenWordsFileNotFoundException();
            }

            forbiddenWords = reader.lines()
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());

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