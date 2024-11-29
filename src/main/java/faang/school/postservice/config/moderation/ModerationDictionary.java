package faang.school.postservice.config.moderation;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class ModerationDictionary {

    @Getter
    private Set<String> curseWords;

    @Value("${spring.scheduler.comment.moderator.path-curse-words}")
    private Path curseWordsPath;

    @PostConstruct
    public void init(){
        try (Stream<String> lines = Files.lines(curseWordsPath)){
            curseWords = lines
                    .flatMap(ModerationDictionary::getStreamWords)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.warn("Dictionary  of curse words hasn't been created", e);
        }
    }

    private static Stream<String> getStreamWords(String text) {
        return Arrays.stream(text.split("\\W+"));
    }

    public boolean checkCurseWordsInComment(String text) {
        return getStreamWords(text).anyMatch(curseWords::contains);
    }
}
