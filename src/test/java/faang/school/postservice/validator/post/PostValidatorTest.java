package faang.school.postservice.validator.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.post.PostWOAuthorException;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

        // Assert
        Assertions.assertThrows(PostWOAuthorException.class, () -> validator.validateAuthor(dtoAuthorNull));
        Assertions.assertThrows(PostWithTwoAuthorsException.class, () -> validator.validateAuthor(dtoBothAuthor));
        assertDoesNotThrow(() -> validator.validateAuthor(dtoWithAuthor));
        assertDoesNotThrow(() -> validator.validateAuthor(dtoWithProject));
    }

    @Test
    void testValidatePublished() {
        Post postIsPublished = new Post();
        Post postIsNotPublished = new Post();
        postIsPublished.setPublished(true);

        Assertions.assertThrows(PostAlreadyPublishedException.class, () -> validator.validatePublished(postIsPublished));
        assertDoesNotThrow(() -> validator.validatePublished(postIsNotPublished));
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

        Assertions.assertThrows(ImmutablePostDataException.class, () -> validator.checkImmutableData(postDto, post));
    }

}
