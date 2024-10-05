package faang.school.postservice.moderation;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;

@Data
@Component
public class Dictionary {

    private final Set<String> dictionary;

    @Value("${post.moderation.dictionary.file-path}")
    private String filePath;

    @PostConstruct
    private void initDictionary() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.toLowerCase().trim());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file", ex.getCause());
        }
    }
}
