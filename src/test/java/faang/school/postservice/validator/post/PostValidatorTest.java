package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.post.PostValidatorException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class PostValidatorTest {
    private PostValidator validator = new PostValidator();

    @Test
    void testValidateAuthor() {
        // Arrange
        Long authorId = 1L;
        Long projectId = 2L;
        PostDto dtoAuthorNull = new PostDto();
        PostDto dtoBothAuthor = new PostDto();
        PostDto dtoWithAuthor = new PostDto();
        PostDto dtoWithProject = new PostDto();
        dtoWithAuthor.setAuthorId(authorId);
        dtoBothAuthor.setAuthorId(authorId);
        dtoBothAuthor.setProjectId(projectId);
        dtoWithProject.setProjectId(projectId);
        boolean flagWithAuthor = true;
        boolean flagWithProject = true;

        // Action
        try {
            validator.validateAuthor(dtoWithProject);
        } catch (PostValidatorException e) {
            flagWithAuthor = false;
        }

        try {
            validator.validateAuthor(dtoWithProject);
        } catch (PostValidatorException e) {
            flagWithProject = false;
        }

        // Assert
        Assertions.assertThrows(PostValidatorException.class, () -> validator.validateAuthor(dtoAuthorNull));
        Assertions.assertThrows(PostValidatorException.class, () -> validator.validateAuthor(dtoBothAuthor));
        assertTrue(flagWithAuthor);
        assertTrue(flagWithProject);
    }

    @Test
    void testValidatePublished() {
        Post postIsPublished = new Post();
        Post postIsNotPublished = new Post();
        postIsPublished.setPublished(true);
        boolean isPublished = true;

        try {
            validator.validatePublished(postIsPublished);
        } catch (PostValidatorException e) {
            isPublished = false;
        }

        Assertions.assertThrows(PostValidatorException.class, () -> validator.validatePublished(postIsNotPublished));
        assertTrue(isPublished);
    }

    @Test
    void testCheckImmutableData() {
        Long postId = 1L;
        Long authorId = 2L;
        Long updateAuthorId = 3L;
        Post post = new Post();
        PostDto postDto = new PostDto();
        post.setId(postId);
        post.setAuthorId(authorId);
        postDto.setId(postId);
        postDto.setAuthorId(updateAuthorId);

        Assertions.assertThrows(PostValidatorException.class, () -> validator.checkImmutableData(postDto, post));
    }

}
