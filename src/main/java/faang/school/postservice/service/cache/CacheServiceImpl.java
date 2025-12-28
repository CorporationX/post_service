package faang.school.postservice.service.cache;

import faang.school.postservice.config.redis.entity.Author;
import faang.school.postservice.repository.redis.AuthorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CacheServiceImpl implements CacheService {
    private final AuthorRepository authorRepository;
    @Value("${redis.ttl.author-cache-second}")
    private Long ttlSecond;

    public void saveAuthor(Long authorId, Long modelId) {
        authorRepository.save(Author.builder()
                .authorId(authorId)
                .modelId(modelId)
                .ttl(ttlSecond)
                .build());
        log.info("Save author to redis cache {}", authorId);
    }

    public Optional<Author> getAuthor(Long authorId) {
        return authorRepository.findByAuthorId(authorId);
    }
}