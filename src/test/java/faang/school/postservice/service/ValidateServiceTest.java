package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ValidateServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @InjectMocks
    private ValidateService validateService;

    @Test
    void validateUser_NullUserId_ThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validateService.validateUser(null),
                "Expected IllegalArgumentException when userId is null"
        );
    }

    @Test
    void validateUser_ValidUserId_CallsUserServiceClient() {
        long userId = 1L;
        validateService.validateUser(userId);
        verify(userServiceClient).getUser(userId);
    }

    @Test
    void validatePost_NullPostId_ThrowsIllegalArgumentException() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validateService.validatePost(null),
                "Expected IllegalArgumentException when postId is null"
        );
    }

    @Test
    void validatePost_ValidPostId_CallsProjectServiceClient() {
        long postId = 2L;
        validateService.validatePost(postId);
        verify(projectServiceClient).getProject(postId);
    }
}