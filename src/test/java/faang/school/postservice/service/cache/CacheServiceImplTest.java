package faang.school.postservice.service.cache;

import faang.school.postservice.config.redis.entity.Author;
import faang.school.postservice.repository.redis.AuthorRepository;
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

    @InjectMocks
    private CacheServiceImpl cacheService;

    private final Long ttlSecond = 300L;

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

    @Test
    void test_saveAuthorComment_successfullySavesAuthorWithCorrectData() {
        Long authorId = 123L;
        Long modelId = 456L;
        CacheServiceImpl service = createServiceWithTtl();

        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.saveAuthorComment(authorId, modelId);

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
}