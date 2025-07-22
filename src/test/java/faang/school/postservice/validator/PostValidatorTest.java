package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.MixedAuthorshipException;
import faang.school.postservice.exception.post.NoAuthorshipException;
import faang.school.postservice.exception.post.RepeatPublishException;
import faang.school.postservice.model.Post;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator postValidator;

    @Test
    void validatePublishThrowsRepeatPublishExceptionIfPostIsAlreadyPublished() {
        Post post = mock(Post.class);
        when(post.isPublished()).thenReturn(true);

        assertThrows(RepeatPublishException.class, () -> postValidator.validatePublish(post));
    }

    @Test
    void validatePublishThrowsEntityNotFoundExceptionIfPostIsDeleted() {
        Post post = mock(Post.class);
        when(post.isPublished()).thenReturn(false);
        when(post.isDeleted()).thenReturn(true);
        when(post.getId()).thenReturn(99L);

        assertThrows(EntityNotFoundException.class, () -> postValidator.validatePublish(post));
    }

    @Test
    void validatePublishSucceedsIfPostIsUnpublishedAndNotDeleted() {
        Post post = mock(Post.class);
        when(post.isPublished()).thenReturn(false);
        when(post.isDeleted()).thenReturn(false);

        assertDoesNotThrow(() -> postValidator.validatePublish(post));
    }

    @Test
    void validateUpdateThrowsEntityNotFoundExceptionIfPostIsDeleted() {
        Post post = mock(Post.class);
        when(post.isDeleted()).thenReturn(true);
        when(post.getId()).thenReturn(100L);

        assertThrows(EntityNotFoundException.class, () -> postValidator.validateUpdate(post));
    }

    @Test
    void validateUpdateSucceedsIfPostIsNotDeleted() {
        Post post = mock(Post.class);
        when(post.isDeleted()).thenReturn(false);

        assertDoesNotThrow(() -> postValidator.validateUpdate(post));
    }

    @Test
    void validateCreateThrowsNoAuthorshipExceptionWhenBothAuthorIdAndProjectIdAreNull() {
        CreatePostDto createPostDto = new CreatePostDto("Test content", null, null);

        assertThrows(NoAuthorshipException.class, () -> postValidator.validateCreate(createPostDto));
    }

    @Test
    void validateCreateThrowsMixedAuthorshipExceptionWhenBothAuthorIdAndProjectIdAreProvided() {
        CreatePostDto createPostDto = new CreatePostDto("Test content", 1L, 2L);

        assertThrows(MixedAuthorshipException.class, () -> postValidator.validateCreate(createPostDto));
    }

    @Test
    void validateCreateCallsCheckUserExistsIfOnlyAuthorIdIsPresent() {
        long authorId = 1L;
        CreatePostDto createPostDto = new CreatePostDto("Test content", authorId, null);
        when(userServiceClient.getUser(authorId)).thenReturn(userDto(authorId));

        assertDoesNotThrow(() -> postValidator.validateCreate(createPostDto));
        verify(userServiceClient).getUser(authorId);
        verifyNoInteractions(projectServiceClient);
    }

    @Test
    void validateCreateCallsCheckProjectExistsIfOnlyProjectIdIsPresent() {
        long projectId = 1L;
        CreatePostDto createPostDto = new CreatePostDto("Test content", null, projectId);
        when(projectServiceClient.getProject(projectId)).thenReturn(projectDto(projectId));

        assertDoesNotThrow(() -> postValidator.validateCreate(createPostDto));
        verify(projectServiceClient).getProject(projectId);
        verifyNoInteractions(userServiceClient);
    }

    @Test
    void checkUserExistsSucceedsSilentlyIfUserServiceClientReturnsNormally() {
        long validAuthorId = 1L;
        when(userServiceClient.getUser(validAuthorId)).thenReturn(userDto(validAuthorId));

        assertDoesNotThrow(() -> postValidator.checkUserExists(validAuthorId));
    }

    @Test
    void checkUserExistsThrowsUserNotExistentExceptionWhenFeign404InUserCheck() {
        long badAuthorId = 1L;
        doThrow(FeignException.NotFound.class).when(userServiceClient).getUser(badAuthorId);

        assertThrows(EntityNotFoundException.class, () -> postValidator.checkUserExists(badAuthorId));
    }

    @Test
    void checkUserExistsThrowsRuntimeExceptionWhenNon404FeignInUserCheck() {
        long invalidAuthorId = 1L;
        doThrow(FeignException.InternalServerError.class).when(userServiceClient).getUser(invalidAuthorId);

        assertThrows(RuntimeException.class, () -> postValidator.checkUserExists(invalidAuthorId));
    }

    @Test
    void checkProjectExistsSucceedsSilentlyIfProjectServiceClientReturnsNormally() {
        long validProjectId = 2L;
        when(projectServiceClient.getProject(validProjectId)).thenReturn(projectDto(validProjectId));

        assertDoesNotThrow(() -> postValidator.checkProjectExists(validProjectId));
    }

    @Test
    void checkProjectExistsThrowsProjectNotExistentExceptionWhenFeign404() {
        long invalidProjectId = 2L;
        doThrow(FeignException.NotFound.class).when(projectServiceClient).getProject(invalidProjectId);

        assertThrows(EntityNotFoundException.class, () -> postValidator.checkProjectExists(invalidProjectId));
    }

    @Test
    void checkProjectExistsThrowsRuntimeExceptionWhenNon404Feign() {
        long projectId = 2L;

        doThrow(FeignException.NotFound.class).when(projectServiceClient).getProject(projectId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> postValidator.checkProjectExists(projectId));
    }

    private UserDto userDto(long userId) {
        return new UserDto(userId,"someName", "someEmail");
    }

    private ProjectDto projectDto(long projectId) {
        return new ProjectDto(projectId, "someTitle");
    }
}
