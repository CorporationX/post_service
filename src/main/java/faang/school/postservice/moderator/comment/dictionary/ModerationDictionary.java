package faang.school.postservice.moderator.comment.dictionary;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Component
public class ModerationDictionary {
    @Value("${file.fileWithBadWords.path}")
    @Setter
    private String pathToFile;
    @Getter
    @Setter
    private Set<String> badWords = new HashSet<>();

    @EventListener(ApplicationReadyEvent.class)
    public void readBadWords() {
        String file = pathToFile;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                badWords.add(line);
            }
        } catch (IOException e) {
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
