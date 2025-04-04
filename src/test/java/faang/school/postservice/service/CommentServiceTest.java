package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.CommentRepository;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    ModerationDictionary moderationDictionary;

    @InjectMocks
    CommentService commentService;

    @Nested
    class ModerateUnverifiedComments{
        @Test
        public void noComments(){
            when(commentRepository.findByVerifiedIsNull()).thenReturn(List.of());

            LogCaptor logCaptor = LogCaptor.forClass(CommentService.class);

            commentService.moderateUnverifiedComments();

            List<String> logs = logCaptor.getInfoLogs();
            assertTrue(logs.contains("No comments to moderate."));
        }

        @Test
        public void withComments(){
            Comment comment = new Comment();
            comment.setContent("clean content");
            ReflectionTestUtils.setField(commentService, "chunkSize", 100);

            when(commentRepository.findByVerifiedIsNull()).thenReturn(List.of(comment));
            LogCaptor logCaptor = LogCaptor.forClass(CommentService.class);

            commentService.moderateUnverifiedComments();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            List<String> logs = logCaptor.getInfoLogs();
            assertTrue(logs.contains("Found 1 unverified comments to process"));
            assertTrue(logs.contains("Moderation finished."));

        }
    }
}