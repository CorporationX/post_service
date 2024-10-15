package faang.school.postservice.dictionary;

import faang.school.postservice.exception.comment.ExceptionMessages;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Component
public class ModerationDictionary {

    private final Set<String> forbiddenWords = new HashSet<>();

    public ModerationDictionary (@Value("${post.moderator.scheduler.dictionary.file}") Resource dictionaryResource) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(dictionaryResource.getInputStream()))) {
            bufferedReader.lines().forEach(line -> forbiddenWords.add(line.trim()));
        } catch (IOException e) {
            throw new RuntimeException(ExceptionMessages.POST_NOT_FOUND.getMessage());
        }
    }
}
