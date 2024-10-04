package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@EnableAsync
class ModerationAsyncServiceTest {
    @Mock
    private ModerationProcessingService moderationProcessingService;

    @InjectMocks
    private ModerationAsyncService moderationAsyncService;

    private List<Post> posts;

    @BeforeEach
    void setUp() {
        Post postOne = Post.builder()
                .content("This is a test post.")
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();

        Post postTwo = Post.builder()
                .content("Another test post.")
                .verificationStatus(VerificationPostStatus.UNVERIFIED)
                .build();

        posts = new ArrayList<>();
        posts.add(postOne);
        posts.add(postTwo);
    }

    @Test
    void testModeratePostsSublistAsync() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(invocation -> {
            latch.countDown();
            return null;
        }).when(moderationProcessingService).moderatePostsSublist(posts);

        moderationAsyncService.moderatePostsSublistAsync(posts);

        // Ждем завершения асинхронного вызова
        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "Асинхронный метод не завершился в течение ожидаемого времени.");

        verify(moderationProcessingService, times(1)).moderatePostsSublist(posts);
    }
}