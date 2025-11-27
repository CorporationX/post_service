package faang.school.postservice.dictionary;

import faang.school.postservice.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ModerationDictionary {
    private final List<String> offensiveWords;

    public ModerationDictionary(@Value("${files-path.offensive-words}") String filePath) {
        this.offensiveWords = loadWordsFromFile(filePath);
    }

    public List<String> loadWordsFromFile(String filePath) {
        List<String> offensiveWordsToReturn = new ArrayList<>();
        File offensiveWordsFile = new File(filePath);
        try (FileReader fileReader = new FileReader(offensiveWordsFile, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            while (bufferedReader.ready()) {
                offensiveWordsToReturn.add(bufferedReader.readLine());
            }
        } catch (IOException e) {
            String errorMessage = "File %s not found".formatted(offensiveWordsFile.getName());
            log.error(errorMessage);
            throw new FileException(errorMessage);
        }
        return offensiveWordsToReturn;
    }

    public boolean hasOffensiveWords(String textToVerify) {
        return StringUtils.containsAny(textToVerify, offensiveWords.toArray(String[]::new));
    }
}