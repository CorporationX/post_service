package faang.school.postservice.publisher;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private PostService postService;

    @Test
    void testPublishScheduledPosts() {
        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 3001; i++) {
            Post post = new Post();
            post.setPublished(false);
            posts.add(post);
        }

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPosts();
        verify(postRepository, times(4)).saveAll(anyList());
    }

}
