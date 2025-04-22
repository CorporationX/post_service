package faang.school.postservice.service;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class PostModerationDictionaryImpl implements ModerationDictionary {

    private static final List<String> FORBIDDEN_WORDS = new CopyOnWriteArrayList<>();

    @Value("${app.dictionary.post-dictionary-path}")
    private String path;

    @PostConstruct
    public void init() {
        try(InputStream inputStream = getClass().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File at " + path + " doesn't exist");
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            FORBIDDEN_WORDS.addAll(bufferedReader.lines().map(String::toLowerCase).toList());
        } catch (IOException e) {
            log.error("InputStream error ", e);
        }
    }

    public boolean isTextWithoutForbiddenWords(@NotBlank String text) {
        String lowerText = text.toLowerCase();
        return FORBIDDEN_WORDS.stream()
                .noneMatch(lowerText::contains);
    }
}
