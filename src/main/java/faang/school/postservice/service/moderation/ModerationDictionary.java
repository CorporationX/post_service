package faang.school.postservice.service.moderation;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ModerationDictionary {

    @Value("${moderation.dictionary}")
    private Resource dictionary;

    @Getter
    private Set<String> moderationSet;

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(dictionary.getInputStream(), StandardCharsets.UTF_8)
        )) {
            moderationSet = reader.lines()
                    .map(line -> line.toLowerCase().trim())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Error reading moderation dictionary", e);
            throw new RuntimeException("Error reading moderation dictionary");
        }
    }

    public boolean containsBadWord(String text) {
        if (text != null) {
            String lowerCaseText = text.toLowerCase();
            return moderationSet.stream().anyMatch(lowerCaseText::contains);
        }
        return false;
    }
}
