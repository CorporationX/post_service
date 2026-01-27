package faang.school.postservice.service.cache;

import faang.school.postservice.config.redis.entity.Author;
import faang.school.postservice.config.redis.entity.PostCache;
import faang.school.postservice.repository.redis.AuthorRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
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
    private final PostCacheRepository postCacheRepository;
    @Value("${redis.ttl.author-cache-second}")
    private Long ttlSecond;
    @Value("${redis.ttl.post-cache-second}")
    private Long postTtlSecond;

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

    @Override
    public void savePost(Long postId, Long authorId, Long projectId) {
        postCacheRepository.save(PostCache.builder()
                .postId(postId)
                .authorId(authorId)
                .projectId(projectId)
                .ttl(postTtlSecond)
                .build());
        log.info("Save post to redis cache, post id: {}, author id: {}", postId, authorId);
    }
}