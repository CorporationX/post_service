package faang.school.postservice.scheduler;

import faang.school.postservice.config.TestContainersConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerificationPostStatus;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

//@SpringBootTest
//@ActiveProfiles("test")
//public class ScheduledPostPublisherTest extends TestContainersConfig {
//
//    @Autowired
//    private ScheduledPostPublisher scheduledPostPublisher;
//
//    @Autowired
//    private PostRepository postRepository;
//
//    @BeforeEach
//    public void init() {
//        LocalDateTime scheduledTime = LocalDateTime.now().minusMinutes(1);
//        IntStream.range(0, 3).forEach(i -> {
//            Post post = Post.builder()
//                    .content("content")
//                    .deleted(false)
//                    .published(false)
//                    .scheduledAt(scheduledTime)
//                    .verificationStatus(VerificationPostStatus.UNVERIFIED)
//                    .build();
//            postRepository.save(post);
//        });
//    }
//
//    @Test
//    public void testScheduledPostPublish() throws InterruptedException {
//        long timeout = 2000L;
//        scheduledPostPublisher.scheduledPostPublish();
//        Thread.sleep(timeout);
//        List<Post> posts = postRepository.findAll();
//        posts.forEach(post -> Assertions.assertTrue(post.isPublished()));
//    }
//}
