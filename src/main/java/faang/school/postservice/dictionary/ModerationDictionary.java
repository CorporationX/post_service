package faang.school.postservice.dictionary;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@PropertySource("classpath:moderation.properties")
public class ModerationDictionary {
    CommentRepository commentRepository;


    @Value("${moderation.file.path}")
    private String filePath;

    public void verifyComment(Comment unverifiedComment) throws IOException {
        Set<String> forbiddenWords = Files.lines(Paths.get(filePath))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (String forbiddenWord : forbiddenWords) {
            if (unverifiedComment.getContent().contains(forbiddenWord)) {
                unverifiedComment.setVerified(false);
                unverifiedComment.setVerifiedDate(LocalDateTime.now());
                commentRepository.save(unverifiedComment);
            }
        }
    }
}
