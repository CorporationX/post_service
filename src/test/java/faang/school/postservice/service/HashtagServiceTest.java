package faang.school.postservice.service;

import faang.school.postservice.model.Hashtag;
import faang.school.postservice.repository.HashtagRepository;
import faang.school.postservice.service.hashtag.HashtagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HashtagServiceTest {
    @InjectMocks
    HashtagService hashtagService;

    @Mock
    HashtagRepository hashtagRepository;

    private List<String> hashtagNames;

    @BeforeEach
    void setUp() {
        hashtagNames = Arrays.asList("#1", "#2", "#3");
    }

    @Test
    @DisplayName("Test saving all hashtags")
    void testSaveAllHashtags() {
        when(hashtagRepository.existsByName(anyString())).thenReturn(false);

        hashtagService.saveAllHashtags(hashtagNames);

        verify(hashtagRepository, times(3)).save(any(Hashtag.class));
    }

    @Test
    @DisplayName("Test getting hashtags by name")
    void testGetHashtagsByName() {
        List<Hashtag> mockHashtags = Arrays.asList(
                Hashtag.builder().name("#1").build(),
                Hashtag.builder().name("#2").build(),
                Hashtag.builder().name("#3").build()
        );

        when(hashtagRepository.findByNameIn(hashtagNames)).thenReturn(mockHashtags);

        List<Hashtag> result = hashtagService.getHashtagsByName(hashtagNames);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(hashtag -> hashtag.getName().equals("#1")));
        assertTrue(result.stream().anyMatch(hashtag -> hashtag.getName().equals("#2")));
        assertTrue(result.stream().anyMatch(hashtag -> hashtag.getName().equals("#3")));

        verify(hashtagRepository, times(1)).findByNameIn(hashtagNames);
    }
}
