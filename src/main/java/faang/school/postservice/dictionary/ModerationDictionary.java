package faang.school.postservice.dictionary;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Getter
@Component
public class ModerationDictionary {

    private final Set<String> forbiddenWords = new HashSet<>();

    public ModerationDictionary (@Value("${post.moderator.scheduler.dictionary.file}") Resource dictionaryResource) {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(dictionaryResource.getInputStream()))) {
            bufferedReader.lines().forEach(line -> forbiddenWords.add(line.trim()));
        } catch (Exception e) {
            log.error("The file {} could not be read", forbiddenWords);
        }
    }
}
