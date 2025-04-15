package faang.school.postservice.util.post;

import faang.school.postservice.api.PerspectiveAPI;
import faang.school.postservice.exception.PostModerationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static faang.school.postservice.service.PostService.MODERATION_FAIL_EXCEPTION;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private PerspectiveAPI perspectiveAPI;
    @InjectMocks
    private PostService postService;

    private final int pageSize = 10;
    private List<Post> testPosts;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "pageSize", pageSize);
        ReflectionTestUtils.setField(postService, "threadPoolSize", 2);
        ReflectionTestUtils.setField(postService, "moderationExecutor", Executors.newSingleThreadExecutor());
    }

    @Test
    void shouldProcessAllPages() throws IOException {
        Page<Post> page1 = new PageImpl<>(
                List.of(createPost(1L, "Content 1")),
                PageRequest.of(0, pageSize),
                1L
        );

        when(postRepository.findByVerifiedDateIsNull(any()))
                .thenReturn(page1)
                .thenReturn(Page.empty());

        postService.moderatePosts();

        verify(perspectiveAPI).isContentToxic("Content 1");
    }

    @Test
    void moderateBatch_ShouldMarkPostAsVerified() throws Exception {
        Post post = createPost(1L, "Good content");
        when(perspectiveAPI.isContentToxic(anyString())).thenReturn(false);

        postService.moderateBatch(List.of(post));

        assertTrue(post.isVerified());
        assertNotNull(post.getVerifiedDate());
        verify(postRepository).save(post);
    }

    @Test
    void moderateBatch_ShouldMarkPostAsToxic() throws Exception {
        Post post = createPost(2L, "Bad words");
        when(perspectiveAPI.isContentToxic(anyString())).thenReturn(true);

        postService.moderateBatch(List.of(post));

        assertFalse(post.isVerified());
        assertNotNull(post.getVerifiedDate());
    }

    @Test
    void moderateBatch_ShouldHandleApiErrors() throws Exception {
        Post post = createPost(3L, "Error content");
        when(perspectiveAPI.isContentToxic(anyString()))
                .thenThrow(new PostModerationException(MODERATION_FAIL_EXCEPTION));

        assertThrows(PostModerationException.class, () ->
                postService.moderateBatch(List.of(post)));
    }

    private Post createPost(Long id, String content) {
        return Post.builder()
                .id(id)
                .content(content)
                .verified(false)
                .verifiedDate(null)
                .build();
    }
}
