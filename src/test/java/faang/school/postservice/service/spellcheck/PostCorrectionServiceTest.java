package faang.school.postservice.service.spellcheck;

import faang.school.postservice.config.properties.SpellCheckAsyncProperties;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostCorrectionServiceTest {

    @Mock
    private PostService postService;

    @Mock
    private AsyncPostSpellCheckerService asyncService;

    @Captor
    private ArgumentCaptor<List<Post>> postBatchCaptor;

    @InjectMocks
    private PostCorrectionServiceImpl service;

    private Post post1;
    private Post post2;
    private Post post3;

    @BeforeEach
    void setup() {
        post1 = new Post();
        post1.setId(1L);
        post2 = new Post();
        post2.setId(2L);
        post3 = new Post();
        post3.setId(3L);
        SpellCheckAsyncProperties properties = new SpellCheckAsyncProperties(
                2,
                5,
                10,
                100
        );
        service = new PostCorrectionServiceImpl(postService, asyncService, properties);
    }

    @Test
    @DisplayName("Should do nothing when no unpublished posts")
    void skipIfNoPosts() {
        when(postService.getUnpublishedPosts()).thenReturn(Collections.emptyList());

        service.correctAllUnpublishedPosts();

        verify(asyncService, never()).correctPostBatchAsync(any());
    }

    @Test
    @DisplayName("Should correct posts in a single batch")
    void processSingleBatch() {
        List<Post> posts = List.of(post1, post2);
        when(postService.getUnpublishedPosts()).thenReturn(posts);

        service.correctAllUnpublishedPosts();

        verify(asyncService).correctPostBatchAsync(posts);
    }

    @Test
    @DisplayName("Should split posts into multiple batches and process each")
    void splitIntoBatches() {
        List<Post> posts = List.of(post1, post2, post3);
        when(postService.getUnpublishedPosts()).thenReturn(posts);

        service.correctAllUnpublishedPosts();

        verify(asyncService, times(2)).correctPostBatchAsync(postBatchCaptor.capture());

        List<List<Post>> batches = postBatchCaptor.getAllValues();

        assertEquals(2, batches.get(0).size());
        assertTrue(batches.get(0).containsAll(List.of(post1, post2)));

        assertEquals(1, batches.get(1).size());
        assertTrue(batches.get(1).contains(post3));
    }
}
