package faang.school.postservice.job;

import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.config.moderation.ModerationProperties;
import faang.school.postservice.job.comment.CommentModerationJob;
import faang.school.postservice.model.comment.Comment;
import faang.school.postservice.service.comment.CommentService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentModerationJobTest {

    @Test
    public void testModerationJob() {
        Comment offensive = new Comment();
        offensive.setId(1L);
        offensive.setContent("слово 1");

        Comment clean = new Comment();
        clean.setId(2L);
        clean.setContent("нормальный комментарий");

        CommentService commentService = mock(CommentService.class);
        ModerationDictionary dictionary = mock(ModerationDictionary.class);

        ModerationProperties props = new ModerationProperties();
        props.setBatchSize(1);
        props.setMaxThreadPoolSize(2);
        props.setCron("0 0 0 * * *");

        when(commentService.getUnverifiedComments()).thenReturn(List.of(offensive, clean));
        when(dictionary.containsOffensive("слово 1")).thenReturn(true);
        when(dictionary.containsOffensive("нормальный комментарий")).thenReturn(false);

        CommentModerationJob job = new CommentModerationJob(commentService, dictionary, props);
        job.moderateCommentsToOffensiveContent();

        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    verify(commentService).delete(1L);

                    ArgumentCaptor<List<Comment>> captor = ArgumentCaptor.forClass(List.class);
                    verify(commentService, times(1)).saveAll(captor.capture());

                    List<Comment> savedComments = captor.getValue();
                    assertEquals(1, savedComments.size());

                    Comment saved = savedComments.get(0);
                    assertEquals(2L, saved.getId(), "Ожидается, что сохранён комментарий с ID=2");
                    assertTrue(saved.getVerified(), "Комментарий должен быть верифицирован");
                    assertNotNull(saved.getVerifiedDate(), "Дата верификации должна быть установлена");
                });
    }
}
