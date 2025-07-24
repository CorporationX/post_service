package faang.school.postservice.validator;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.MixedAuthorshipException;
import faang.school.postservice.exception.post.NoAuthorshipException;
import faang.school.postservice.exception.post.RepeatPublishException;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.post.ProjectFeignService;
import faang.school.postservice.service.post.UserFeignService;
import org.junit.jupiter.api.Nested;
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
    private UserFeignService userFeignService;

    @Mock
    private ProjectFeignService projectFeignService;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private PostValidator postValidator;

    @Nested
    class ValidatePublishTests {
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
    }

    @Nested
    class ValidateUpdateTests {
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
    }

    @Nested
    class ValidateCreateTests {
        @Test
        void validateCreateThrowsNoAuthorshipExceptionWhenBothAuthorIdAndProjectIdAreNull() {
            CreatePostDto createPostDto = new CreatePostDto("Test content", null, null);

            assertThrows(NoAuthorshipException.class, () -> postValidator.validateCreate(createPostDto));
        }

        @Test
        void validateCreateThrowsMixedAuthorshipExceptionWhenBothAuthorIdAndProjectIdAreProvided() {
            CreatePostDto createPostDto = new CreatePostDto("Test content", 1L, 2L);

            assertThrows(MixedAuthorshipException.class, () -> postValidator.validateCreate(createPostDto));
            verifyNoInteractions(userFeignService);
            verifyNoInteractions(projectFeignService);
        }

        @Test
        void validateCreateCallsCheckUserExistsIfOnlyAuthorIdIsPresent() {
            long authorId = 1L;
            CreatePostDto createPostDto = new CreatePostDto("Test content", authorId, null);
            when(userFeignService.getUserOrFail(authorId)).thenReturn(userDto(authorId));

            assertDoesNotThrow(() -> postValidator.validateCreate(createPostDto));
            verify(userFeignService).getUserOrFail(authorId);
            verifyNoInteractions(projectServiceClient);
        }

        @Test
        void validateCreateCallsCheckProjectExistsIfOnlyProjectIdIsPresent() {
            long projectId = 1L;
            CreatePostDto createPostDto = new CreatePostDto("Test content", null, projectId);
            when(projectFeignService.getProjectOrFail(projectId)).thenReturn(projectDto(projectId));

            assertDoesNotThrow(() -> postValidator.validateCreate(createPostDto));
            verify(projectFeignService).getProjectOrFail(projectId);
            verifyNoInteractions(userFeignService);
        }
    }

    @Nested
    class CheckUserTests {
        @Test
        void checkUserExistsSucceedsSilentlyIfCanGetUser() {
            long validAuthorId = 1L;
            when(userFeignService.getUserOrFail(validAuthorId)).thenReturn(userDto(validAuthorId));

            assertDoesNotThrow(() -> postValidator.checkUserExists(validAuthorId));
        }

        @Test
        void checkUserExistsThrowsWhenCannotGetUser() {
            long badAuthorId = 1L;
            doThrow(EntityNotFoundException.class).when(userFeignService).getUserOrFail(badAuthorId);

            assertThrows(EntityNotFoundException.class, () -> postValidator.checkUserExists(badAuthorId));
        }
    }

    @Nested
    class CheckProjectTests {

        @Test
        void checkProjectExistsSucceedsSilentlyIfCanGetProject() {
            long validProjectId = 2L;
            when(projectFeignService.getProjectOrFail(validProjectId)).thenReturn(projectDto(validProjectId));

            assertDoesNotThrow(() -> postValidator.checkProjectExists(validProjectId));
        }

        @Test
        void checkProjectExistsThrowsIfCannotGetProject() {
            long invalidProjectId = 2L;
            doThrow(EntityNotFoundException.class).when(projectFeignService).getProjectOrFail(invalidProjectId);

            assertThrows(EntityNotFoundException.class, () -> postValidator.checkProjectExists(invalidProjectId));
        }
    }

    private UserDto userDto(long userId) {
        return new UserDto(userId,"someName", "someEmail");
    }

    private ProjectDto projectDto(long projectId) {
        return new ProjectDto(projectId, "someTitle");
    }
}
