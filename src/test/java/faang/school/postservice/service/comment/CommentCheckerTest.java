package faang.school.postservice.service.comment;

import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentCheckerTest {

    private CommentChecker commentChecker;

    @Mock
    private CommentModerationDictionary moderationDictionary;

    @BeforeEach
    public void setUp() {
        Set<String> obsceneWords = Set.of("word1", "word2", "word3");
        when(moderationDictionary.getObsceneWords()).thenReturn(obsceneWords);
        commentChecker = new CommentChecker(moderationDictionary);
        //commentChecker.init(moderationDictionary);
    }

    @Test
    @DisplayName("Accurate search bad words in the comment")
    public void testAccurateSearchBadWords() {
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .content("Some text with bad word1")
                .build();

        assertFalse(commentChecker.isAcceptableComment(comment));
    }

    @Test
    @DisplayName("Searching comment without bad words")
    public void testSearchWithoutBadWords() {
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .content("Some text")
                .build();

        assertTrue(commentChecker.isAcceptableComment(comment));
    }

    @Test
    @DisplayName("Fuzzy search bad words with one mistake")
    public void testSearchBadWordsWithOneMistake() {
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .content("Some text with bad wor*1")
                .build();

        assertFalse(commentChecker.isAcceptableComment(comment));
    }

    @Test
    @DisplayName("Fuzzy search bad words with two mistakes")
    public void testSearchBadWordsWithTwoMistakes() {
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .content("Some text with bad wo**3")
                .build();

        assertFalse(commentChecker.isAcceptableComment(comment));
    }
}
