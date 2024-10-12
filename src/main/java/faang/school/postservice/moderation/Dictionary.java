package faang.school.postservice.moderation;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.nio.file.Paths;

@Data
@Component
@RequiredArgsConstructor
public class Dictionary {

    private final Set<String> dictionary;

    @Autowired
    public Dictionary(@Value("${post.moderation.dictionary.file-path}") String filePath) {

        dictionary = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dictionary.add(line.toLowerCase().trim());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read file - " + Paths.get(filePath).getFileName(), ex.getCause());
        }
    }
}
