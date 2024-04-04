package faang.school.postservice.validation.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

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
    private UserDto userDto;
    private ProjectDto projectDto;

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
        userDto = UserDto.builder()
                .id(2L)
                .username("Valid username")
                .email("valid@email.com")
                .build();
        projectDto = ProjectDto.builder()
                .id(3L)
                .title("Valid title")
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
    void validateIfAuthorExists_AuthorExists_ShouldNotThrow() {
        when(userServiceClient.getUser(postDto.getAuthorId())).thenReturn(userDto);

        assertDoesNotThrow(() ->
                postValidator.validateIfAuthorExists(postDto));
    }

    @Test
    void validateIfAuthorExists_ProjectExists_ShouldNotThrow() {
        postDto.setAuthorId(null);
        postDto.setProjectId(17L);
        when(projectServiceClient.findProjectById(postDto.getProjectId())).thenReturn(projectDto);

        assertDoesNotThrow(() ->
                postValidator.validateIfAuthorExists(postDto));
    }

    @Test
    void validateIfAuthorExists_AuthorDoesntExist_ShouldThrowEntityNotFoundException() {
        when(userServiceClient.getUser(postDto.getAuthorId())).thenThrow(FeignException.class);

        assertThrows(EntityNotFoundException.class, () ->
                postValidator.validateIfAuthorExists(postDto));
    }

    @Test
    void validateIfAuthorExists_ProjectDoesntExist_ShouldThrowEntityNotFoundException() {
        postDto.setAuthorId(null);
        postDto.setProjectId(17L);
        when(projectServiceClient.findProjectById(postDto.getProjectId())).thenThrow(FeignException.class);

        assertThrows(EntityNotFoundException.class, () ->
                postValidator.validateIfAuthorExists(postDto));

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

    @Test
    void validateImagesCount_ImagesLimitExceeded_ThrowsDataValidationException() throws NoSuchFieldException, IllegalAccessException {
        Field maxImages = postValidator.getClass().getDeclaredField("maxImages");
        maxImages.setAccessible(true);
        maxImages.set(postValidator, 10);
        assertThrows(DataValidationException.class, () -> postValidator.validateImagesCount(11));
    }

    @Test
    void validateImagesCount2_ImagesLimitExceeded_ThrowsDataValidationException() throws NoSuchFieldException, IllegalAccessException {
        Field maxImages = postValidator.getClass().getDeclaredField("maxImages");
        maxImages.setAccessible(true);
        maxImages.set(postValidator, 10);
        assertThrows(DataValidationException.class, () -> postValidator.validateImagesCount(5, 10));
    }
}
