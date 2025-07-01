package faang.school.postservice.job;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.job.comment.CommentModerationJob;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentModerationJobTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentModerationJob commentModerationJob;

    @MockBean
    private ModerationDictionary moderationDictionary;

    @Test
    void testModerationJobProcessesCommentsCorrectly() {
        Comment offensive = new Comment();
        offensive.setContent("some BAD_WORD here");
        offensive.setVerified(false);
        offensive.setInProgress(false);
        commentRepository.save(offensive);

        Comment clean = new Comment();
        clean.setContent("everything is fine");
        clean.setVerified(false);
        clean.setInProgress(false);
        commentRepository.save(clean);

        Mockito.when(moderationDictionary.containsOffensive("some BAD_WORD here")).thenReturn(true);
        Mockito.when(moderationDictionary.containsOffensive("everything is fine")).thenReturn(false);

        commentModerationJob.moderateCommentsToOffensiveContent();

        List<Comment> remaining = StreamSupport
                .stream(commentRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        assertThat(remaining)
                .hasSize(1)
                .allSatisfy(comment -> {
                    assertThat(comment.getContent()).isEqualTo("everything is fine");
                    assertThat(comment.getVerified()).isTrue();
                    assertThat(comment.isInProgress()).isFalse();
                    assertThat(comment.getVerifiedDate()).isNotNull();
                });
    }
}
