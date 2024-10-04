package faang.school.postservice.dictionary;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
public class ModerationDictionary {
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    @Bean
    public ExecutorService moderatorPool() {
        return Executors.newFixedThreadPool(10);
    }

    @Async("moderatorPool")
    public void verifyComment(Comment unverifiedComment) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:forbidden-words.txt");
        Set<String> forbiddenWords = Files.lines(Paths.get(resource.getFile().getPath()))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        if (forbiddenWords.stream().anyMatch(unverifiedComment.getContent()::contains)) {
            unverifiedComment.setVerified(false);
            unverifiedComment.setVerifiedDate(LocalDateTime.now());
            commentRepository.save(unverifiedComment);
        } else {
            unverifiedComment.setVerifiedDate(LocalDateTime.now());
            unverifiedComment.setVerified(true);
            commentRepository.save(unverifiedComment);
        }
    }
}
