package faang.school.postservice.service.post;

import faang.school.postservice.exception.post.UserBanException;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.MessagePublisher;
import faang.school.postservice.service.post.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

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
        ReflectionTestUtils.setField(postService, "banPostLimit", 5);
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_Success() {
        List<Long> authorIdsToBan = Arrays.asList(1L, 2L, 3L);

        when(postRepository.findAuthorIdsToBan(5)).thenReturn(authorIdsToBan);

        postService.banAuthorsWithUnverifiedPostsMoreThan();

        for (Long authorId : authorIdsToBan) {
            verify(messagePublisher, times(1)).publish(authorId);
        }

        verifyNoMoreInteractions(messagePublisher);
    }

    @Test
    void testBanAuthorsWithUnverifiedPostsMoreThan_NoAuthorsToBan() {
        when(postRepository.findAuthorIdsToBan(5)).thenReturn(Arrays.asList());

        postService.banAuthorsWithUnverifiedPostsMoreThan();

        verify(messagePublisher, never()).publish(anyLong());
    }
}