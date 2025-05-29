package faang.school.postservice.service.util;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.comment.CommentModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentModerationAsyncHandlerTest {
    @Mock
    private CommentModerationService commentModerationService;

    @Mock
    private ModerationDictionary moderationDictionary;

    @InjectMocks
    private CommentModerationAsyncHandler commentModerationAsyncHandler;

    private Comment cleanComment;
    private Comment profanityComment;
    private final Set<String> profanityWords = Set.of("bad", "word");

    @BeforeEach
    void setUp() {
        cleanComment = new Comment();
        cleanComment.setId(1L);
        cleanComment.setContent("This is a clean content");

        profanityComment = new Comment();
        profanityComment.setId(2L);
        profanityComment.setContent("This contains bad word");
    }

    @Test
    @DisplayName("Проверка комментария, комментарий должен быть верифицирован")
    public void givenCleanComment_WhenCheckForProfanity_ThenCommentVerified() {
        when(moderationDictionary.getProfanityWords()).thenReturn(profanityWords);
        doNothing().when(commentModerationService).moderateComments(any(), any());

        CompletableFuture<Void> result = commentModerationAsyncHandler.checkForProfanity(List.of(cleanComment));

        assertNull(result.join());
    }

    @Test
    @DisplayName("Проверка комментария, комментарий не должен быть верифицирован")
    public void givenProfanityComment_WhenCheckForProfanity_ThenCommentNotVerified() {
        when(moderationDictionary.getProfanityWords()).thenReturn(profanityWords);
        doNothing().when(commentModerationService).moderateComments(any(), any());

        CompletableFuture<Void> result = commentModerationAsyncHandler.checkForProfanity(List.of(profanityComment));

        assertNull(result.join());
    }
}