package faang.school.postservice.validation.comment;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CommentValidatorTest {

    private final CommentValidator validator = new CommentValidator();

    private static final long POST_ID = 100L;
    private static final long AUTHOR_ID = 1L;
    private static final long POST_AUTHOR_ID = 2L;
    private static final long THIRD_ID = 3L;

    @Test
    @DisplayName("Should do nothing if post exists")
    void shouldPassIfPostExists() {
        assertDoesNotThrow(() -> validator.ensurePostExists(true, POST_ID));
    }

    @Test
    @DisplayName("Should throw exception if post does not exist")
    void shouldThrowIfPostNotExists() {
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                validator.ensurePostExists(false, POST_ID));

        assertEquals("Post not found with id: " + POST_ID, ex.getMessage());
    }

    @Test
    @DisplayName("Should pass if comment belongs to the post")
    void shouldPassIfCommentBelongsToPost() {
        Comment comment = buildComment(AUTHOR_ID, POST_ID);

        assertDoesNotThrow(() -> validator.ensureCommentBelongsToPost(comment, POST_ID));
    }

    @Test
    @DisplayName("Should throw exception if comment does not belong to the post")
    void shouldThrowIfCommentDoesNotBelongToPost() {
        Comment comment = buildComment(AUTHOR_ID, 999L);

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                validator.ensureCommentBelongsToPost(comment, POST_ID));

        assertEquals("Comment does not belong to post with id: " + POST_ID, ex.getMessage());
    }

    @Test
    @DisplayName("Should pass if user is the author of the comment")
    void shouldPassIfUserIsAuthor() {
        assertDoesNotThrow(() -> validator.ensureUserIsAuthor(AUTHOR_ID, AUTHOR_ID));
    }

    @Test
    @DisplayName("Should throw exception if user is not the author of the comment")
    void shouldThrowIfUserIsNotAuthor() {
        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                validator.ensureUserIsAuthor(AUTHOR_ID, POST_AUTHOR_ID));

        assertEquals("User is not the author of this comment", ex.getMessage());
    }

    @Test
    @DisplayName("Should pass if user is the author of the comment")
    void shouldPassIfUserIsAuthorOfComment() {
        Post post = buildPost(POST_AUTHOR_ID);
        Comment comment = buildComment(AUTHOR_ID, POST_ID);

        assertDoesNotThrow(() -> validator.ensureUserCanDeleteComment(comment, post, AUTHOR_ID));
    }

    @Test
    @DisplayName("Should pass if user is the author of the post")
    void shouldPassIfUserIsAuthorOfPost() {
        Post post = buildPost(AUTHOR_ID);
        Comment comment = buildComment(POST_AUTHOR_ID, POST_ID);

        assertDoesNotThrow(() -> validator.ensureUserCanDeleteComment(comment, post, AUTHOR_ID));
    }

    @Test
    @DisplayName("Should pass if user is author of both comment and post")
    void shouldPassIfUserIsAuthorOfBoth() {
        Post post = buildPost(AUTHOR_ID);
        Comment comment = buildComment(AUTHOR_ID, POST_ID);

        assertDoesNotThrow(() -> validator.ensureUserCanDeleteComment(comment, post, AUTHOR_ID));
    }

    @Test
    @DisplayName("Should throw exception if user is not author of comment or post")
    void shouldThrowIfUserIsNotAuthorOfEither() {
        Post post = buildPost(POST_AUTHOR_ID);
        Comment comment = buildComment(AUTHOR_ID, POST_ID);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                validator.ensureUserCanDeleteComment(comment, post, THIRD_ID));

        assertEquals("User is not allowed to delete this comment", ex.getMessage());
    }

    private Post buildPost(long authorId) {
        return Post.builder()
                .id(POST_ID)
                .authorId(authorId)
                .build();
    }

    private Comment buildComment(long authorId, long postId) {
        Post post = Post.builder()
                .id(postId)
                .build();
        return Comment.builder()
                .authorId(authorId)
                .post(post)
                .build();
    }
}
