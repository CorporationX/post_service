package faang.school.postservice.service.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.comment.FileReadException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Getter
@Component
public class ModerationDictionary {

    private Set<String> obsceneWords;

    @Value("${comment.obscene-words-resource}")
    private String obsceneWordsResource;

    @PostConstruct
    public void init() {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(obsceneWordsResource)) {
            obsceneWords = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new FileReadException(String.format("The file %s could not be read", obsceneWordsResource), e);
        }
    }
}
