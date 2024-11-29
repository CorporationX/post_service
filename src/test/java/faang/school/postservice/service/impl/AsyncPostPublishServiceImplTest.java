package faang.school.postservice.service.impl;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.post.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.cache.NewsFeedAsyncCacheService;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.PostCacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AsyncPostPublishServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private NewsFeedAsyncCacheService newsFeedAsyncCacheService;

    @Mock
    private PostCacheService cachePostRepository;

    @Spy
    private PostMapperImpl postMapper;

    @InjectMocks
    private AsyncPostPublishServiceImpl asyncPostPublishService;

    @Test
    void publishPost() {
        long postId = 1L;
        Post post = Post.builder()
                .id(postId)
                .content("content")
                .authorId(1L)
                .published(false)
                .build();
        List<Post> posts = List.of(post);

        asyncPostPublishService.publishPost(posts);

        verify(postRepository).saveAll(posts);
        verify(cachePostRepository).save(eq(postId), any(PostDto.class));
    }
}

