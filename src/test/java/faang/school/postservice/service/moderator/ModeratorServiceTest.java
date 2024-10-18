package faang.school.postservice.service.moderator;

import faang.school.postservice.config.dictionary.OffensiveWordsDictionary;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.comment.PublishedCommentEventPublisher;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ModeratorServiceTest {

    private static final int TWO_TIMES_USED = 2;
    private static final int FOUR_TIMES_USED = 4;

    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 1L;
    private static final long USER_ID = 1L;
    private static final long FIVE_SECOND_AWAIT = 5L;

    @Mock
    private CommentService commentService;

    @Mock
    private OffensiveWordsDictionary offensiveWordsDictionary;

    @Mock
    private PublishedCommentEventPublisher publishedCommentEventPublisher;

    @Mock
    private CommentMapper commentMapper;

    private ModeratorService moderatorService;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Post post;
    private Comment comment;
    private Comment comment1;
    private List<Comment> unverifiedComments;

    @BeforeEach
    void init() {
        moderatorService = new ModeratorService(commentService,
                commentMapper,
                executorService,
                offensiveWordsDictionary,
                publishedCommentEventPublisher);

        post = Post.builder()
                .id(POST_ID)
                .authorId(USER_ID)
                .build();

        comment = Comment.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .post(post)
                .content("test test1")
                .build();

        comment1 = Comment.builder()
                .id(COMMENT_ID)
                .authorId(USER_ID)
                .post(post)
                .content("a b")
                .build();

        unverifiedComments = Arrays.asList(comment, comment1);
    }

    @Test
    @DisplayName("When no offensive words then update comment and save")
    void whenNoOffensiveWordsThenUpdateAndSaveComments() {
        when(commentService.getUnverifiedComments())
                .thenReturn(unverifiedComments);
        when(offensiveWordsDictionary.isWordContainsInDictionary(anyString()))
                .thenReturn(Boolean.FALSE);

        moderatorService.moderateCommentsContent();

        verify(commentService).getUnverifiedComments();
        verify(commentService).saveComments(anyList());

        assertEquals(Boolean.TRUE, comment.isVerified());
        assertEquals(Boolean.TRUE, comment1.isVerified());

        await().atMost(FIVE_SECOND_AWAIT, SECONDS).untilAsserted(() -> {

            verify(offensiveWordsDictionary, times(FOUR_TIMES_USED)).isWordContainsInDictionary(anyString());

            verify(publishedCommentEventPublisher, times(TWO_TIMES_USED)).publish(any());
        });
    }

    @Test
    @DisplayName("When offensive words contains then update comment and save")
    void whenOffensiveWordsThenUpdateAndSaveComments() {
        when(commentService.getUnverifiedComments())
                .thenReturn(unverifiedComments);
        when(offensiveWordsDictionary.isWordContainsInDictionary(anyString()))
                .thenReturn(Boolean.TRUE);

        moderatorService.moderateCommentsContent();

        verify(commentService).getUnverifiedComments();
        verify(commentService).saveComments(anyList());

        assertEquals(Boolean.FALSE, comment.isVerified());
        assertEquals(Boolean.FALSE, comment1.isVerified());

        await().atMost(FIVE_SECOND_AWAIT, SECONDS).untilAsserted(() -> {
            verify(offensiveWordsDictionary, times(FOUR_TIMES_USED)).isWordContainsInDictionary(anyString());
            verify(publishedCommentEventPublisher, never()).publish(any());
        });
    }
}
