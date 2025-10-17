package faang.school.postservice.jobs;

import faang.school.postservice.jobs.correctorpost.ScheduledCorrectorPostsAsync;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledCorrectorPostsTest {
    private static final long COUNT_DRAFT_POSTS = 110L;
    private static final int TEST_BATCH_SIZE = 20;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private ScheduledCorrectorPostsAsync scheduledCorrectorPosts;

    private List<Post> posts;
    private Post testPost;

    @BeforeEach
    void setUp() {
        posts = new ArrayList<>();
        testPost = Post.builder()
                .id(1L)
                .content("content")
                .build();
        posts.add(testPost);
    }

    @Test
    void testScheduledCorrectorPostsWhenWorkingJob() {
        ReflectionTestUtils.setField(scheduledCorrectorPosts, "batchSize", TEST_BATCH_SIZE);
        when(postRepository.countDraftPosts()).thenReturn(COUNT_DRAFT_POSTS);
        doNothing().when(postService).correctingContentBatchPostsAsync(anyInt());

        scheduledCorrectorPosts.correctingSpellingOfPosts();

        verify(postRepository).countDraftPosts();
        verify(postService, times(6)).correctingContentBatchPostsAsync(TEST_BATCH_SIZE);
    }

    @Test
    void testScheduledCorrectorPostsWhenEmptyPosts() {
        ReflectionTestUtils.setField(scheduledCorrectorPosts, "batchSize", TEST_BATCH_SIZE);
        when(postRepository.countDraftPosts()).thenReturn(0L);

        scheduledCorrectorPosts.correctingSpellingOfPosts();

        verify(postRepository).countDraftPosts();
        verify(postService, never()).correctingContentBatchPostsAsync(TEST_BATCH_SIZE);
    }
}
