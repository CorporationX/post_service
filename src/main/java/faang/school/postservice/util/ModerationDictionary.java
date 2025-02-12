package faang.school.postservice.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ModerationDictionary {
    private final Set<String> bannedWords;

    public ModerationDictionary(@Value("${moderation.banned-words-path}") String filePath) throws IOException {
        String content = Files.readString(Path.of(filePath));
        ObjectMapper objectMapper = new ObjectMapper();
        this.bannedWords = new HashSet<>(objectMapper.readValue(content, new TypeReference<List<String>>() {}));
    }

    public boolean containsBannedWords(String content) {
        return content != null && !content.isBlank() &&
                Set.of(content.toLowerCase().split("\\W+")).stream().anyMatch(bannedWords::contains);
    }
}