package faang.school.postservice.service.post.impl;

import faang.school.postservice.publisher.MessagePublisher;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
public class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MessagePublisher<Long> banUserPublisher;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_Success() {
        int banPostLimit = 5;
        List<Long> authorIdsToBan = Arrays.asList(1L, 2L, 3L);

        when(postRepository.findAuthorIdsToBan(banPostLimit)).thenReturn(authorIdsToBan);

        postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);

        for (Long authorId : authorIdsToBan) {
            verify(banUserPublisher, times(1)).publish(authorId);
        }
        verifyNoMoreInteractions(banUserPublisher);
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_NoAuthorsToBan() {
        int banPostLimit = 5;
        List<Long> authorIdsToBan = Arrays.asList();

        when(postRepository.findAuthorIdsToBan(banPostLimit)).thenReturn(authorIdsToBan);

        postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);

        verify(banUserPublisher, times(0)).publish(anyLong());
        verifyNoMoreInteractions(banUserPublisher);
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_WithException() {
        int banPostLimit = 5;
        List<Long> authorIdsToBan = Arrays.asList(1L, 2L);

        when(postRepository.findAuthorIdsToBan(banPostLimit)).thenReturn(authorIdsToBan);

        doNothing().when(banUserPublisher).publish(1L);
        doThrow(new RuntimeException("Redis error")).when(banUserPublisher).publish(2L);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);
        });

        assertEquals("Redis error", exception.getMessage());

        verify(banUserPublisher, times(1)).publish(1L);
        verify(banUserPublisher, times(1)).publish(2L);
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_PostRepositoryThrowsException() {
        int banPostLimit = 5;

        when(postRepository.findAuthorIdsToBan(banPostLimit)).thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);
        });

        assertEquals("Database error", exception.getMessage());

        verify(banUserPublisher, times(0)).publish(anyLong());
    }
}
