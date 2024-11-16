package faang.school.postservice.service.impl;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CachePostRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncPostPublishServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CachePostRepository cachePostRepository;

    @InjectMocks
    private AsyncPostPublishServiceImpl asyncPostPublishService;

    @Test
    void publishPost() {
        Post post = Post.builder()
                .content("content")
                .authorId(1L)
                .published(false)
                .build();
        List<Post> posts = List.of(post);

        asyncPostPublishService.publishPost(posts);

        verify(postRepository).saveAll(posts);
        verify(cachePostRepository).save(post.getAuthorId().toString(), post.getId());
    }
}
