package faang.school.postservice.service.comment.sort;

import faang.school.postservice.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortByUpdateDescendingTest {
    private CommentSortingStrategy sorter;

    private final Comparator<Comment> comparator =
            Comparator.comparing(Comment::getUpdatedAt, Comparator.reverseOrder());

    @BeforeEach
    void setUp() {
        sorter = new SortByUpdateDescending();
    }

    @Test
    @DisplayName("Getting sorted list of comments")
    void sortByUpdateDescendingTest_GetSortedComments() {
        List<Comment> comments = initComments();

        var result = sorter.getSortedComments(comments);

        assertThat(result).isSortedAccordingTo(comparator);
        assertEquals(comments.size(), result.size());
        assertTrue(result.containsAll(comments));
    }

    @Test
    @DisplayName("Getting sorted single comment")
    void sortByUpdateDescendingTest_GetSortedSingleComment() {
        Comment comment = initComment(1L, LocalDateTime.of(2022, 1, 1, 1, 1));

        var result = sorter.getSortedComments(List.of(comment));

        assertEquals(1, result.size());
        assertTrue(result.contains(comment));
    }

    @Test
    @DisplayName("Getting sorted empty list of comments")
    void sortByUpdateDescendingTest_GetSortedEmptyList() {
        List<Comment> comments = List.of();

        var result = sorter.getSortedComments(comments);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Getting sorted list of comments with null arguments")
    void sortByUpdateDescendingTest_GetSortedNullArguments() {
        var ex = assertThrows(NullPointerException.class, () -> sorter.getSortedComments(null));
        assertEquals("comments is marked non-null but is null", ex.getMessage());
    }

    Comment initComment(Long id, LocalDateTime updatedAt) {
        return Comment.builder()
                .id(id)
                .updatedAt(updatedAt)
                .build();
    }

    List<Comment> initComments() {
        return List.of(
                initComment(1L, LocalDateTime.of(2022, 1, 1, 1, 4)),
                initComment(2L, LocalDateTime.of(2022, 1, 1, 1, 2)),
                initComment(3L, LocalDateTime.of(2022, 1, 1, 1, 1)),
                initComment(4L, LocalDateTime.of(2022, 1, 1, 1, 3)));
    }
}
