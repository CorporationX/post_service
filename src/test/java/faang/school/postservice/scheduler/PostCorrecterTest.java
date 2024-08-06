package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostCorrecterTest {

    @InjectMocks
    private PostCorrecter postCorrecter;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostService postService;

    private final int BATCH_SIZE = 10;

    private List<Post> generatePosts(int count) {
        List<Post> posts = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            posts.add(Post.builder()
                    .id(i).build());
        }
        return posts;
    }

    @BeforeEach
    public void setUp() {
        postCorrecter.setBATCH_SIZE(BATCH_SIZE);
    }

    @Test
    public void testCorrectPosts() {
        List<Post> posts = generatePosts(25);
        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postCorrecter.correctPosts();

        verify(postService, times(1))
                .correctPostsContent(posts.subList(0, BATCH_SIZE));
        verify(postService, times(1))
                .correctPostsContent(posts.subList(BATCH_SIZE, 2 * BATCH_SIZE));
        verify(postService, times(1))
                .correctPostsContent(posts.subList(2 * BATCH_SIZE, posts.size()));
    }
}