package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class ScheduledPostPublisherTest {

    @Mock
    ThreadPoolExecutor threadPoolExecutor;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private ScheduledPostPublisher scheduledPostPublisher;

    private Post firstPost;
    private Post secondPost;
    private Post thirdPost;

    @BeforeEach
    void setUp() {
        firstPost = Post.builder()
                .published(false)
                .deleted(false)
                .scheduledAt(LocalDateTime.now().minusMonths(1))
                .build();
        secondPost = Post.builder()
                .published(false)
                .deleted(true)
                .scheduledAt(LocalDateTime.now().minusDays(1))
                .build();
        thirdPost = Post.builder()
                .published(false)
                .deleted(false)
                .scheduledAt(LocalDateTime.now().plusMonths(3))
                .build();
    }

//    @Test
//    void publishSchedulePostsFirstScenarioTest() {
//        List<Post> verifyExpected = List.of(firstPost);
//
//        when(postRepository.findReadyToPublish()).thenReturn(verifyExpected);
//
//        scheduledPostPublisher.publishScheduledPosts();
//
//        verify(postRepository).findReadyToPublish();
//        verify(postRepository).saveAll(verifyExpected);
//
//        assertTrue(firstPost.isPublished());
//    }
//
//    @Test
//    void publishSchedulePostsSecondScenarioTest() {
//        List<Post> returnList = new ArrayList<>();
//
//        when(postRepository.findReadyToPublish()).thenReturn(returnList);
//
//        scheduledPostPublisher.publishScheduledPosts();
//
//        verify(threadPoolExecutor, times(2)).execute(any());
//        verify(threadPoolExecutor).shutdown();
//    }
}