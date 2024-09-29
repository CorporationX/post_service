package faang.school.postservice.service.comment.sort;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortByLikesCountAscendingTest {
    private CommentSortingStrategy sorter;

    private final Comparator<Comment> comparator = Comparator.comparing(comment -> comment.getLikes().size());

    @BeforeEach
    public void setUp() {
        sorter = new SortByLikesCountAscending();
    }

    @Test
    @DisplayName("Getting sorted list of comments")
    void sortByUpdateDescendingTest_GetSortedComments() {
        List<Comment> comments = initComments();

        var result = sorter.apply(comments);

        assertThat(result).isSortedAccordingTo(comparator);
        assertEquals(comments.size(), result.size());
        assertTrue(result.containsAll(comments));
    }

    @Test
    @DisplayName("Getting sorted single comment")
    void sortByUpdateDescendingTest_GetSortedSingleComment() {
        Comment comment = initComment(1L, initLikes(15));

        var result = sorter.apply(List.of(comment));

        assertEquals(1, result.size());
        assertTrue(result.contains(comment));
    }

    @Test
    @DisplayName("Getting sorted empty list of comments")
    void sortByUpdateDescendingTest_GetSortedEmptyList() {
        List<Comment> comments = List.of();

        var result = sorter.apply(comments);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Getting sorted list of comments with null arguments")
    void sortByUpdateDescendingTest_GetSortedNullArguments() {
        var ex = assertThrows(NullPointerException.class, () -> sorter.apply(null));
        assertEquals("comments is marked non-null but is null", ex.getMessage());
    }

    List<Like> initLikes(int count) {
        List<Like> likes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            likes.add(Like.builder().build());
        }
        return likes;
    }

    Comment initComment(Long id, List<Like> likes) {
        return Comment.builder()
                .id(id)
                .likes(likes)
                .build();
    }

    List<Comment> initComments() {
        return List.of(
                initComment(1L, initLikes(25)),
                initComment(2L, initLikes(10)),
                initComment(3L, initLikes(2)),
                initComment(4L, initLikes(15)));
    }
}
