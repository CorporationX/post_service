package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostPublisherTest {

    private ExecutorService directExecutor;

    @Mock
    private PostRepository postRepository;

    @Mock
    private BatchPublisher batchPublisher;

    @InjectMocks
    private PostService postService;

    @BeforeEach
    public void setUp() {
        directExecutor = new AbstractExecutorService() {
            @Override
            public void shutdown() {
            }

            @Override
            public @NotNull List<Runnable> shutdownNow() {
                return Collections.emptyList();
            }

            @Override
            public boolean isShutdown() {
                return false;
            }

            @Override
            public boolean isTerminated() {
                return false;
            }

            @Override
            public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
                return false;
            }

            @Override
            public void execute(@NotNull Runnable command) {
                command.run();
            }
        };
        ReflectionTestUtils.setField(
                postService,
                "postPublisherPool",
                directExecutor
        );
    }


    @Test
    public void publishScheduledPostsNoReadyPostsNothingHappens() {
        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());
        postService.publishScheduledPosts();
        verify(postRepository, never()).saveAll(anyList());
    }

    @Test
    void publishScheduledPostsSingleBatchAllPostsDelegatedToBatchPublisher() {
        List<Post> ready = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Post p = new Post();
            p.setPublished(false);
            p.setScheduledAt(LocalDateTime.now().minusHours(1));
            ready.add(p);
        }
        when(postRepository.findReadyToPublish()).thenReturn(ready);

        postService.publishScheduledPosts();

        ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
        verify(batchPublisher, times(1)).publishBatch(captor.capture());
        List<Post> passed = captor.getValue();
        assertThat(passed).hasSize(3);
        assertThat(passed).isEqualTo(ready);
    }

    @Test
    void publishScheduledPostsMultipleBatchesDelegatedCorrectBatchSizes() {
        int total = 2500;
        List<Post> ready = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            Post p = new Post();
            p.setPublished(false);
            p.setScheduledAt(LocalDateTime.now().minusHours(1));
            ready.add(p);
        }
        when(postRepository.findReadyToPublish()).thenReturn(ready);

        postService.publishScheduledPosts();

        ArgumentCaptor<List<Post>> captor = ArgumentCaptor.forClass(List.class);
        verify(batchPublisher, times(3)).publishBatch(captor.capture());
        List<List<Post>> batches = captor.getAllValues();

        assertThat(batches.get(0)).hasSize(1000);
        assertThat(batches.get(1)).hasSize(1000);
        assertThat(batches.get(2)).hasSize(500);

        assertThat(batches.get(0)).containsExactlyElementsOf(ready.subList(0, 1000));
        assertThat(batches.get(1)).containsExactlyElementsOf(ready.subList(1000, 2000));
        assertThat(batches.get(2)).containsExactlyElementsOf(ready.subList(2000, 2500));
    }
}
