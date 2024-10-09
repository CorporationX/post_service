package faang.school.postservice.moderation;

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

    private Set<String> dictionary;

    private String filePath;

    public Dictionary(@Value("${post.moderation.dictionary.file-path}") String filePath, Set<String> dictionary) {
        this.filePath = filePath;

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
