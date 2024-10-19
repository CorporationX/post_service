package faang.school.postservice.service.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.comment.FileReadException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Getter
@Component
public class CommentModerationDictionary {

    private final Set<String> obsceneWords;

    public CommentModerationDictionary(@Value("${comment.obscene-words-resource}") String obsceneWordsResource,
                                ObjectMapper objectMapper) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(obsceneWordsResource)) {
            obsceneWords = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new FileReadException(String.format("The file %s could not be read", obsceneWordsResource), e);
        }
    }
}
