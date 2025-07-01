package faang.school.postservice.job;

import faang.school.postservice.config.TestContainersConfig;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.entity.post.Post;
import faang.school.postservice.job.comment.CommentModerationJob;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
class CommentModerationJobTest extends TestContainersConfig {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentModerationJob commentModerationJob;

    @MockBean
    private ModerationDictionary moderationDictionary;

    @Test
    void testModerationJobProcessesCommentsCorrectly() {
        long authorId = 123L;

        Post post = new Post();
        post.setAuthorId(authorId);
        post.setTitle("test");
        post.setContent("test");
        post = postRepository.save(post);

        Comment offensive = new Comment();
        offensive.setContent("some некомпетентный here");
        offensive.setVerified(false);
        offensive.setInProgress(false);
        offensive.setAuthorId(authorId);
        offensive.setPost(post);
        commentRepository.save(offensive);

        Comment clean = new Comment();
        clean.setContent("everything is fine");
        clean.setVerified(false);
        clean.setInProgress(false);
        clean.setAuthorId(authorId);
        clean.setPost(post);
        commentRepository.save(clean);

        Mockito.when(moderationDictionary.containsOffensive("some некомпетентный here")).thenReturn(true);
        Mockito.when(moderationDictionary.containsOffensive("everything is fine")).thenReturn(false);

        commentModerationJob.moderateCommentsToOffensiveContent();

        await()
            .atMost(5, SECONDS)
            .untilAsserted(() -> {
                List<Comment> remaining = StreamSupport
                        .stream(commentRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());

                assertThat(remaining)
                        .hasSize(1)
                        .allSatisfy(comment -> {
                            assertThat(comment.getContent()).isEqualTo("everything is fine");
                            assertThat(comment.getVerified()).isTrue();
                            assertThat(comment.getInProgress()).isFalse();
                        });
            });
    }
}
