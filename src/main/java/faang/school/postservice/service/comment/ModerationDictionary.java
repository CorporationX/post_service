package faang.school.postservice.service.comment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Comment;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.StringTokenizer;

@Component
@Getter
public class ModerationDictionary {

    Set<String> obsceneWords;

    @Value("${comment.obscene-words-resource}")
    private String obsceneWordsResource;

    @PostConstruct
    public void init() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(obsceneWordsResource);
        if (inputStream == null) {
            throw new IllegalArgumentException("obscene-words resource not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            obsceneWords = objectMapper.readValue(inputStream, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAcceptableComment(Comment comment) {
        StringTokenizer tokenizer = new StringTokenizer(comment.getContent(), " ,.!?;:()[]{}\"'\\/\n\t\r");
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().toLowerCase();
            if (obsceneWords.contains(word)) {
                return false;
            }
        }
        return true;
    }
}
