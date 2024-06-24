package faang.school.postservice.service;

import faang.school.postservice.jpa.HashtagJpaRepository;
import faang.school.postservice.model.Hashtag;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.HashtagRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HashtagServiceTest {

    private static final Long POST_ID = 1L;
    private static final String HASHTAG_NAME = "Java";

    @Mock
    private HashtagJpaRepository hashtagJpaRepository;

    @Mock
    private HashtagRepository hashtagRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private HashtagService hashtagService;

    private Post post;
    Hashtag hashtagJava;
    Cache cache;


    @BeforeEach
    public void init() {
        post = Post.builder()
                .id(POST_ID)
                .content("#Java oop cross platform #language with wide #possibilities")
                .build();
        hashtagJava = Hashtag.builder()
                .name(HASHTAG_NAME)
                .build();
        cache = Mockito.mock(Cache.class);
    }


    @Test
    @DisplayName("Create not exists hashtags from post")
    public void createNotExistHashtagFromPost() {

        when(hashtagJpaRepository.findByName(HASHTAG_NAME)).thenReturn(Optional.empty());
        when(cacheManager.getCache(Mockito.anyString())).thenReturn(cache);

        hashtagService.parsePostAndCreateHashtags(post);

        verify(cache, times(3)).evict(Mockito.anyString());
        verify(hashtagJpaRepository, times(3)).save(Mockito.any(Hashtag.class));
    }

    @Test
    @DisplayName("When hashtag already exists in database")
    public void whenHashtagExists() {

        when(hashtagJpaRepository.findByName(HASHTAG_NAME)).thenReturn(Optional.of(hashtagJava));
        when(cacheManager.getCache(Mockito.anyString())).thenReturn(cache);

        hashtagService.parsePostAndCreateHashtags(post);

        verify(cache, times(3)).evict(Mockito.anyString());
        verify(hashtagJpaRepository, times(3)).save(Mockito.any(Hashtag.class));
    }

    @Test
    @DisplayName("When find hashtag by name exists")
    public void whenFindByNameExists() {
        when(hashtagRepository.findByName(HASHTAG_NAME)).thenReturn(hashtagJava);

        Hashtag actualResult = hashtagService.findByName(HASHTAG_NAME);

        assertEquals(hashtagJava, actualResult);
    }

    @Test
    @DisplayName("When find hashtag by name not exists")
    public void whenFindByNameNotExists() {
        doThrow(EntityNotFoundException.class).when(hashtagRepository).findByName(HASHTAG_NAME);

        assertThrows(EntityNotFoundException.class, () -> hashtagService.findByName(HASHTAG_NAME));
    }
}