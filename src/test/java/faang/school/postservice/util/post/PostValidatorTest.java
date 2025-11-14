package faang.school.postservice.util.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class PostValidatorTest {

    @InjectMocks
    private PostValidator postValidator;

    @Test
    public void testUserIsInvalid() {
        final long currentUserId = 3L;
        final PostDto postDto = new PostDto(
                null,
                "content",
                1L,
                null,
                false,
                null,
                false
        );

        assertThrows(DataValidationException.class, () -> postValidator.validateUser(currentUserId, postDto));
    }

    @Test
    public void testUserIsValid() {
        final long currentUserId = 3L;
        final PostDto postDto = new PostDto(
                null,
                "content",
                currentUserId,
                null,
                false,
                null,
                false
        );

        postValidator.validateUser(currentUserId, postDto);
    }

    @Test
    public void testProjectIsInvalid() {
        final long ownerId = 3L;
        final long currentUserId = 5L;
        final long projectId = 1L;

        assertThrows(DataValidationException.class,
                () -> postValidator.validateProject(ownerId, currentUserId, projectId));
    }

    @Test
    public void testProjectIsValid() {
        final long ownerId = 3L;
        final long currentUserId = 3L;
        final long projectId = 1L;

        postValidator.validateProject(ownerId, currentUserId, projectId);
    }

    @Test
    public void testAuthorIdAndProjectIdIsNull() {
        final PostDto postDto = new PostDto(
                null,
                "content",
                null,
                null,
                false,
                null,
                false
        );

        assertThrows(DataValidationException.class, () -> postValidator.validateIds(postDto));
    }

    @Test
    public void testAuthorIdAndProjectIdIsNotNull() {
        final PostDto postDto = new PostDto(
                null,
                "content",
                3L,
                1L,
                false,
                null,
                false
        );

        assertThrows(DataValidationException.class, () -> postValidator.validateIds(postDto));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "3, NULL",
            "NULL, 1"
    }, nullValues = "NULL")
    public void testAuthorIdAndProjectIdIsCorrect(Long authorId, Long projectId) {
        final PostDto postDto = new PostDto(
                null,
                "content",
                authorId,
                projectId,
                false,
                null,
                false
        );

        postValidator.validateIds(postDto);
    }

    @Test
    public void testUnpublishedPostIsInvalid() {
        final Post post = new Post();
        post.setId(5L);
        post.setPublished(false);

        assertThrows(DataValidationException.class, () -> postValidator.validatePostIsUnpublished(post));
    }

    @Test
    public void testUnpublishedPostIsValid() {
        final Post post = new Post();
        post.setId(5L);
        post.setPublished(true);

        postValidator.validatePostIsUnpublished(post);
    }

    @Test
    public void testPublishedPostIsInvalid() {
        final Post post = new Post();
        post.setId(5L);
        post.setPublished(true);

        assertThrows(DataValidationException.class, () -> postValidator.validatePostIsPublished(post));
    }

    @Test
    public void testPublishedPostIsValid() {
        final Post post = new Post();
        post.setId(5L);
        post.setPublished(false);

        postValidator.validatePostIsPublished(post);
    }

    @Test
    public void testDeletedPostIsInvalid() {
        final Post post = new Post();
        post.setId(5L);
        post.setDeleted(true);

        assertThrows(DataValidationException.class, () -> postValidator.validatePostIsDeleted(post));
    }

    @Test
    public void testDeletedPostIsValid() {
        final Post post = new Post();
        post.setId(5L);
        post.setDeleted(false);

        postValidator.validatePostIsDeleted(post);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "3, NULL, NULL, NULL",
            "3, 5, NULL, NULL",
            "NULL, NULL, 3, NULL",
            "NULL, NULL, 3, 5",
            "3, 3, NULL, 5"
    }, nullValues = "NULL")
    public void testValidateChangeAuthor(Long currentAuthorId, Long newAuthorId,
                                         Long currentProjectId, Long newProjectId) {
        final Post currentPost = new Post();
        currentPost.setId(5L);
        currentPost.setAuthorId(currentAuthorId);
        currentPost.setProjectId(currentProjectId);
        final PostDto postDto = new PostDto(
                5L,
                "content",
                newAuthorId,
                newProjectId,
                false,
                null,
                false
        );

        assertThrows(DataValidationException.class, () -> postValidator.validateChangeAuthor(currentPost, postDto));
    }
}
