package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScheduledPostPublisherTest {

    @Mock
    ThreadPoolExecutor threadPoolExecutor;

    @Mock
    private PostRepository postRepository;

    private ScheduledPostPublisher scheduledPostPublisher;

    private Post firstPost;
    private Post secondPost;

    @BeforeEach
    void setUp() {
        scheduledPostPublisher = new ScheduledPostPublisher(postRepository, threadPoolExecutor);
        scheduledPostPublisher.setBatchSize(2);
        firstPost = Post.builder()
                .published(false)
                .deleted(false)
                .scheduledAt(LocalDateTime.now().minusMonths(1))
                .build();
        secondPost = Post.builder()
                .published(false)
                .deleted(false)
                .scheduledAt(LocalDateTime.now().plusMonths(3))
                .build();
    }

    @Test
    void publishSchedulePostsFirstScenarioTest() {
        List<Post> verifyExpected = List.of(firstPost);

        when(postRepository.findReadyToPublish()).thenReturn(verifyExpected);

        scheduledPostPublisher.publishScheduledPosts();

        verify(postRepository).findReadyToPublish();
        verify(postRepository).saveAll(verifyExpected);

        assertTrue(firstPost.isPublished());
    }

    @Test
    void publishSchedulePostsSecondScenarioTest() {
        List<Post> highCapacityList = List.of(firstPost, secondPost, secondPost);

        when(postRepository.findReadyToPublish()).thenReturn(highCapacityList);

        scheduledPostPublisher.publishScheduledPosts();

        verify(threadPoolExecutor, times(2)).execute(any());
    }
}