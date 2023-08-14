package faang.school.postservice.service.comment;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ModerationDictionary moderationDictionary;
    @InjectMocks
    private CommentService commentService;


    @Test
    void testModerateComment() {
        ReflectionTestUtils.setField(commentService, "batchSize", 100);
        List<Comment> comments = createCommentList();
        when(moderationDictionary.containsUnwantedWords("valid")).thenReturn(false);
        when(moderationDictionary.containsUnwantedWords("not valid")).thenReturn(true);
        when(commentRepository.findNotVerified()).thenReturn(comments);

        commentService.moderateComment();

        assertTrue(comments.get(0).isVerified());
        assertFalse(comments.get(1).isVerified());

        verify(commentRepository).saveAll(anyCollection());
    }

    private List<Comment> createCommentList() {
        return List.of(Comment.builder().id(1).content("valid").verified(false).build(),
                Comment.builder().id(2).content("not valid").verified(false).build());
    }
}