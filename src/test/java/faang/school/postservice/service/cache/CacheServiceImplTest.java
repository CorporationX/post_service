package faang.school.postservice.service.cache;

import faang.school.postservice.config.redis.entity.Author;
import faang.school.postservice.config.redis.entity.PostCache;
import faang.school.postservice.repository.redis.AuthorRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheServiceImplTest {
    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PostCacheRepository postCacheRepository;

    @InjectMocks
    private CacheServiceImpl cacheService;

    private final Long ttlSecond = 300L;
    private final Long postTtlSecond = 86400L;

    private CacheServiceImpl createServiceWithTtl() {
        try {
            var field = CacheServiceImpl.class.getDeclaredField("ttlSecond");
            field.setAccessible(true);
            field.set(cacheService, ttlSecond);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cacheService;
    }

    private CacheServiceImpl createServiceWithPostTtl() {
        try {
            var field = CacheServiceImpl.class.getDeclaredField("postTtlSecond");
            field.setAccessible(true);
            field.set(cacheService, postTtlSecond);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return cacheService;
    }

    @Test
    void test_saveAuthorComment_successfullySavesAuthorWithCorrectData() {
        Long authorId = 123L;
        Long modelId = 456L;
        CacheServiceImpl service = createServiceWithTtl();

        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.saveAuthor(authorId, modelId);

        ArgumentCaptor<Author> captor = ArgumentCaptor.forClass(Author.class);
        verify(authorRepository, times(1)).save(captor.capture());

        Author savedAuthor = captor.getValue();
        assertEquals(authorId, savedAuthor.getAuthorId());
        assertEquals(modelId, savedAuthor.getModelId());
        assertEquals(ttlSecond, savedAuthor.getTtl());
    }

    @Test
    void test_getAuthor_whenAuthorExists_returnsOptionalWithAuthor() {
        Long authorId = 999L;
        Author expectedAuthor = Author.builder()
                .authorId(authorId)
                .modelId(777L)
                .ttl(ttlSecond)
                .build();

        when(authorRepository.findByAuthorId(authorId)).thenReturn(Optional.of(expectedAuthor));

        Optional<Author> result = cacheService.getAuthor(authorId);

        assertTrue(result.isPresent());
        assertEquals(authorId, result.get().getAuthorId());
        assertEquals(777L, result.get().getModelId());
        verify(authorRepository, times(1)).findByAuthorId(authorId);
    }

    @Test
    void test_getAuthor_whenAuthorNotExists_returnsEmptyOptional() {
        Long authorId = 888L;

        when(authorRepository.findByAuthorId(authorId)).thenReturn(Optional.empty());

        Optional<Author> result = cacheService.getAuthor(authorId);

        assertFalse(result.isPresent());
        verify(authorRepository, times(1)).findByAuthorId(authorId);
        verifyNoMoreInteractions(authorRepository);
    }

    @Test
    void test_savePost_successfullySavesPostWithCorrectData() {
        Long postId = 100L;
        Long authorId = 200L;
        Long projectId = 300L;
        CacheServiceImpl service = createServiceWithPostTtl();

        when(postCacheRepository.save(any(PostCache.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.savePost(postId, authorId, projectId);

        ArgumentCaptor<PostCache> captor = ArgumentCaptor.forClass(PostCache.class);
        verify(postCacheRepository, times(1)).save(captor.capture());

        PostCache savedPost = captor.getValue();
        assertEquals(postId, savedPost.getPostId());
        assertEquals(authorId, savedPost.getAuthorId());
        assertEquals(projectId, savedPost.getProjectId());
        assertEquals(postTtlSecond, savedPost.getTtl());
    }
}