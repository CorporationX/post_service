package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentModerationServiceTest {
    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentModerationService commentModerationService;

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
    @DisplayName("Комментарий без мата должен быть верифицирован")
    public void givenCleanComment_whenModerate_thenCommentIsVerified() {
        commentModerationService.moderateComments(List.of(cleanComment), profanityWords);

        assertTrue(cleanComment.isVerified());
        assertNotNull(cleanComment.getVerifiedAt());
        verify(commentRepository).saveAll(List.of(cleanComment));
    }

    @Test
    @DisplayName("Комментарий с матом не должен быть верифицирован")
    public void givenProfanityComment_whenModerate_thenCommentIsNotVerified() {
        commentModerationService.moderateComments(List.of(profanityComment), profanityWords);

        assertFalse(profanityComment.isVerified());
        assertNotNull(profanityComment.getVerifiedAt());
        verify(commentRepository).saveAll(List.of(profanityComment));
    }
}
