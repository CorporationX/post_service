package faang.school.postservice.moderator;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
@Slf4j
public class PostModerationDictionary {
    private final Set<String> forbiddenWords = new HashSet<>();
    private Pattern pattern;

    @PostConstruct
    public void init() throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary/postModerationDictionary.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                forbiddenWords.add(line.trim());
            }
        } catch (NullPointerException e) {
            log.error("Не удалось найти файл запрещенных слов", e);
            throw new IOException("Не удалось найти файл запрещенных слов: " + e.getMessage());
        }

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("dictionary/forbidden-regex.regex")) {
            String regexContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            pattern = Pattern.compile(regexContent, Pattern.UNICODE_CHARACTER_CLASS);
        } catch (IOException e) {
            log.error("Ошибка при загрузке файла регулярных выражений: ", e);
            throw new RuntimeException("Ошибка при загрузке файла регулярных выражений: " +  e.getMessage());
        }
    }

    public boolean containsForbiddenWord(String content) {
        for (String word : forbiddenWords) {
            if (content.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // через регулярку вроде лучше получается, если нет, то удалю
    public boolean containsForbiddenWordRegex(String content) {
        return pattern.matcher(content).find();
    }
}
