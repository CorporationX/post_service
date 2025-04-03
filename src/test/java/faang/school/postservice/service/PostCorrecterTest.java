package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PostCorrecterTest {

    @Mock
    private PostService postService;

    @InjectMocks
    private PostCorrecter postCorrecter;

    @Test
    public void testCorrectUnpublishedPosts() {
        postCorrecter.correctUnpublishedPosts();

        verify(postService, times(1)).correctUnpublishedPosts();
    }
}
