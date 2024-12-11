package faang.school.postservice.service.moderation;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.DictionaryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

@Component
public class ModerationDictionary {

    @Value("${moderation.dictionary.path}")
    private String pathToDictionary;

    @Value("${moderation.dictionary.text-split-regex}")
    private String regex;

    private final ObjectMapper mapper;
    private Set<String> moderationDictionary;

    @Autowired
    public ModerationDictionary(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() throws IOException {
        moderationDictionary = mapper.readValue(new File(pathToDictionary), DictionaryWrapper.class)
                .getDictionary();
    }

    public boolean isVerified(String text) {
        return Arrays.stream(text.split(regex))
                .noneMatch(word -> moderationDictionary.contains(word));
    }
}
