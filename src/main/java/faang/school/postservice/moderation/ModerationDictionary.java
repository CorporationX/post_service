package faang.school.postservice.moderation;

import faang.school.postservice.model.Post;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ModerationDictionary {

    private final List<String> dictionary;

    @Value("${post.moderation.dictionary.file-path}")
    private String filePath;

    @Autowired
    public ModerationDictionary(List<String> dictionary) {
        this.dictionary = dictionary;
    }

    @PostConstruct
    private void createDictionary() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.toLowerCase().trim());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file", ex.getCause());
        }
    }

    public List<Post> searchSwearWords(List<Post> unverifiedPost) {
        unverifiedPost.forEach(post -> {
            boolean containsSwearWord = dictionary.stream()
                    .anyMatch(word -> post.getContent().contains(word));

            post.setVerified(!containsSwearWord);
            post.setVerifiedDate(LocalDateTime.now());
        });

        return unverifiedPost;
    }
}
