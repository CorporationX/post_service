package faang.school.postservice.job.moderator;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.BaseContextTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class ModerationSchedulerIntegrationTest extends BaseContextTest {

    @Autowired
    private ModerationScheduler moderationScheduler;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    @AfterEach
    public void clean() {
        postRepository.deleteAll();
    }

    @Test
    public void moderatePosts_success() {
        String badContent = "погода - жопа";
        long idBad = 1L;
        Post postWithBadWords = Post.builder()
                .id(idBad)
                .content(badContent)
                .build();
        String goodContent = "погода прекрасна";
        long idGood = 2L;
        Post postWithoutBadWords = Post.builder()
                .id(idGood)
                .content(goodContent)
                .build();
        postRepository.save(postWithBadWords);
        postRepository.save(postWithoutBadWords);

        moderationScheduler.moderatePosts();

        Post moderatedPost = postRepository.findById(idBad).get();
        assertEquals(
                "погода - ****",
                moderatedPost.getContent());
        assertFalse(moderatedPost.getIsVerified());

        moderatedPost = postRepository.findById(idGood).get();
        assertEquals(
                goodContent,
                moderatedPost.getContent());
        assertTrue(moderatedPost.getIsVerified());
    }
}