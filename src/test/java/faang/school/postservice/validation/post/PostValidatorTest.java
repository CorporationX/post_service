package faang.school.postservice.validation.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    ProjectServiceClient projectServiceClient;
    @InjectMocks
    PostValidator postValidator;

    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .content("Valid content")
                .authorId(10L)
                .build();
        postDto = PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorId(post.getAuthorId())
                .build();
    }

    @Test
    void validatePostAuthor_PostAuthorIsValid_ShouldNotThrow() {
        assertDoesNotThrow(() ->
                postValidator.validatePostAuthor(postDto));

    }

    @Test
    void validatePostAuthor_PostHasNoAuthors_ShouldThrowDataValidationException() {
        postDto.setAuthorId(null);

        assertThrows(DataValidationException.class, () ->
                postValidator.validatePostAuthor(postDto));
    }

    @Test
    void validatePostAuthor_PostHasBothAuthorAndProject_ShouldThrowDataValidationException() {
        postDto.setProjectId(7L);

        assertThrows(DataValidationException.class, () ->
                postValidator.validatePostAuthor(postDto));
    }

    @Test
    void validateIfPostIsPublished_PostNotPublished_ShouldNotThrow() {
        assertDoesNotThrow(() ->
                postValidator.validateIfPostIsPublished(post));
    }

    @Test
    void validateIfPostIsPublished_PostIsPublished_ShouldThrowDataValidationException() {
        post.setPublished(true);

        assertThrows(DataValidationException.class, () ->
                postValidator.validateIfPostIsPublished(post));
    }

    @Test
    void validateUpdatedPost_UpdatedPostIsValid_ShouldNotThrow() {
        postDto.setContent("Updated content");

        assertDoesNotThrow(() ->
                postValidator.validateUpdatedPost(post, postDto));
    }

    @Test
    void validateUpdatedPost_TryingToChangeAuthor_ShouldThrowDataValidationException() {
        postDto.setContent("Updated content");
        postDto.setAuthorId(null);
        postDto.setProjectId(10L);

        assertThrows(DataValidationException.class, () ->
                postValidator.validateUpdatedPost(post, postDto));
    }
}
