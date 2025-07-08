package faang.school.postservice.moderation.model;

import faang.school.postservice.config.moderation.CommentsModerationConfiguration;
import faang.school.postservice.config.moderation.ModerationDictionary;
import faang.school.postservice.jobs.moderation.CommentModerationJob;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentModerationJobTest {

    @Mock
    private CommentService commentService;

    @Mock
    private ModerationDictionary moderationDictionary;

    @Mock
    private CommentsModerationConfiguration configuration;

    @Mock
    private ThreadPoolTaskExecutor executor;

    @InjectMocks
    private CommentModerationJob commentModerationJob;

    private List<Comment> comments;

    @BeforeEach
    void setUp() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .content("Clean comment")
                .build();

        Comment comment2 = Comment.builder()
                .id(2L)
                .content("Bad comment")
                .build();

        comments = Arrays.asList(comment1, comment2);

        when(configuration.getBatchSize()).thenReturn(2);

        doAnswer(invocation -> {
            Callable<?> task = invocation.getArgument(0);
            FutureTask<?> futureTask = new FutureTask<>(task);
            futureTask.run();
            return futureTask;
        }).when(executor).submit(any(Callable.class));
    }

    @Test
    void testModerateCommentsShouldProcessAllBatches() {
        when(commentService.getNotVerifiedComments()).thenReturn(comments);
        when(moderationDictionary.containsProfanity("Clean comment")).thenReturn(false);
        when(moderationDictionary.containsProfanity("Bad comment")).thenReturn(true);

        commentModerationJob.moderateComments();

        ArgumentCaptor<List<Comment>> captor = ArgumentCaptor.forClass(List.class);

        verify(commentService).getNotVerifiedComments();
        verify(commentService, times(1)).saveVerifiedComments(captor.capture());

        List<Comment> result = captor.getValue();
        assertEquals(2, result.size());

        Comment cleanComment = result.get(0);
        Comment badComment = result.get(1);
        assertEquals(1L, cleanComment.getId());
        assertEquals(2L, badComment.getId());
    }
}