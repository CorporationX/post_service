package faang.school.postservice.service.post;

import faang.school.postservice.exception.comment.UserBanException;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.MessagePublisher;
import faang.school.postservice.service.post.impl.PostServiceImpl;
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
    private MessagePublisher messagePublisher;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan() {
        int banPostLimit = 5;
        List<Long> authorIdsToBan = Arrays.asList(1L, 2L, 3L);

        when(postRepository.findAuthorIdsToBan(banPostLimit)).thenReturn(authorIdsToBan);

        postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);

        for (Long authorId : authorIdsToBan) {
            verify(messagePublisher, times(1)).publish(authorId);
        }

        verifyNoMoreInteractions(messagePublisher);
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_WithException() {
        int banPostLimit = 5;
        List<Long> authorIdsToBan = Arrays.asList(1L, 2L);

        when(postRepository.findAuthorIdsToBan(banPostLimit)).thenReturn(authorIdsToBan);

        doNothing().when(messagePublisher).publish(1L);
        doThrow(new RuntimeException("Redis error")).when(messagePublisher).publish(2L);

        UserBanException exception = assertThrows(UserBanException.class, () -> {
            postService.banAuthorsWithUnverifiedPostsMoreThan(banPostLimit);
        });

        assertEquals("Failed to publish ban event for author ID 2", exception.getMessage());
        assertNotNull(exception.getCause());
        assertEquals("Redis error", exception.getCause().getMessage());

        verify(messagePublisher, times(1)).publish(1L);
        verify(messagePublisher, times(1)).publish(2L);
    }
}