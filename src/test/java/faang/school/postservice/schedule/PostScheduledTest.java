package faang.school.postservice.schedule;

import faang.school.postservice.service.PostCorrecterService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostScheduledTest {

    @Mock
    private PostCorrecterService postCorrecterService;

    @InjectMocks
    private PostScheduled postScheduled;

    @Test
    void testCorrectContentPosts() {
        postScheduled.correctContentPosts();

        verify(postCorrecterService).correctAllPosts();
    }
}
