package faang.school.postservice.moderation.dictionary;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
@Slf4j
public class ModerationDictionary {

    @Value("${file.fileWithBadWords.path}")
    @Setter
    private String pathToFile;

    @Getter
    @Setter
    private Set<String> badWords = new HashSet<>();

    @PostConstruct
    public void readBadWords() {
        String file = pathToFile;

        log.info("Start read from badWords file");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                badWords.add(line);
            }
        } catch (IOException e) {
            log.error("IO Exception occurred {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public boolean isContainsBadWordInTheText(String text) {
        String cleanText = text.replaceAll("\\p{Punct}", "");

        String[] words = cleanText.split("\\s+");

        for (String word : words) {
            if (badWords.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
