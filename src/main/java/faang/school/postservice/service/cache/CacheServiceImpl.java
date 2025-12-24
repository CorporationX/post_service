package faang.school.postservice.service.cache;

import faang.school.postservice.config.redis.entity.Author;
import faang.school.postservice.repository.redis.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CacheServiceImpl implements CacheService {
    private final AuthorRepository authorRepository;
    @Value("${data.redis.ttl.author-cache-second}")
    private Long ttlSecond;

    public void saveAuthorComment(Long authorId, Long modelId) {
        authorRepository.save(Author.builder()
                .authorId(authorId)
                .modelId(modelId)
                .ttl(ttlSecond)
                .build());
    }

    public Optional<Author> getAuthor(Long authorId) {
        return authorRepository.findByAuthorId(authorId);
    }
}