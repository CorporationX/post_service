package faang.school.postservice.service.post;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ModerationDictionary {

    @Value("classpath:files/profanities.txt")
    private Resource dictionaryFile;
    private final Set<String> profaneWords = new HashSet<>();

    @PostConstruct
    public void loadDictionary() throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(dictionaryFile.getInputStream(), StandardCharsets.UTF_8))) {
            profaneWords.addAll(reader.lines().collect(Collectors.toSet()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (NullPointerException e) {
            log.error("profanities dictionary file is null", e);
        }
    }

    public boolean containsProfanity(String content) {
        return Arrays.stream(content.toLowerCase().split(" "))
                .anyMatch(profaneWords::contains);
    }
}
