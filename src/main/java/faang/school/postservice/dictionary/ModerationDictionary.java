package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {

    @Getter
    Set<String> forbiddenWords = new HashSet<>();

    @Value("${post.moderator.scheduler.dictionary.file}")
    Resource dictionaryResource;

    @PostConstruct
    public void init() throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(dictionaryResource.getInputStream()))) {
            bufferedReader.lines().forEach(line -> forbiddenWords.add(line.trim()));
        }
    }
}
