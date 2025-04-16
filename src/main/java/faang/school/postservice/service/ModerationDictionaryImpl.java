package faang.school.postservice.service;

import faang.school.postservice.exception.ModerationDictionaryException;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ModerationDictionaryImpl implements ModerationDictionary {

    private final List<String> deniedWordsList = new ArrayList<>();

    @Value("${dictionary.comments.path_to_file}")
    private String pathToDictionary;

    @PostConstruct
    public void init() {
        try (InputStream inputStream = getClass().getResourceAsStream(pathToDictionary)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + pathToDictionary);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                deniedWordsList.addAll(reader.lines().map(String::toLowerCase).toList());
            }
        } catch (IOException e) {
            throw new ModerationDictionaryException(e);
        }
    }

    @Override
    public boolean isTextAreCorrect(@NotBlank String text) {
        String lowerCaseText = text.toLowerCase();
        return deniedWordsList.stream().noneMatch(lowerCaseText::contains);
    }
}