package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.AsyncPostPublishService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncPostPublishServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private AsyncPostPublishServiceImpl asyncPostPublishService;

    @Test
    void publishPost() {
        List<Post> posts = List.of(Post.builder().content("content").authorId(1L).published(false).build());
        asyncPostPublishService.publishPost(posts);
        verify(postRepository, times(1)).saveAll(posts);
    }
}
