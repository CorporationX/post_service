package faang.school.postservice.service.moderation;

import faang.school.postservice.model.Comment;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Component
@Setter
public class ModerationDictionary {
    private final Set<String> profanityWords = new HashSet<>();

    @Value("classpath:profanity-words.txt")
    private Resource profanityWordsFile;

    @PostConstruct
    public void initialize() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(profanityWordsFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                profanityWords.add(line.trim().toLowerCase());
            }
        }
    }

    public void checkComment(Comment comment) {
        String[] words = comment.getContent().toLowerCase().split("\\s+");
        comment.setVerifiedDate(LocalDateTime.now());
        for (String word : words) {
            if (profanityWords.contains(word)) {
                comment.setVerified(false);
                return;
            }
        }
        comment.setVerified(true);
    }
}
