package faang.school.postservice.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {
    @Getter
    private final Set<String> badWords = new HashSet<>();
    @Value("${post.moderator.scheduler.dictionary.file}")
    private Resource dictionaryFile;

    @PostConstruct
    public void init() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dictionaryFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line.trim());
            }

        }
    }
}
