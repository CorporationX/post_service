package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.exception.FeignClientException;
import faang.school.postservice.exception.handler.FeignExceptionHandler;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectFeignServiceTest {

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private FeignExceptionHandler feignExceptionHandler;

    @InjectMocks
    private ProjectFeignService projectFeignService;

    private final Long projectId = 1L;
    private final String projectTitle = "My project";

    @Test
    void getProjectOrFailReturnsProjectWhenOK() {

        when(projectServiceClient.getProject(projectId)).thenReturn(new ProjectDto(projectId, projectTitle));

        ProjectDto result = projectFeignService.getProjectOrFail(projectId);

        verify(projectServiceClient).getProject(projectId);
        assertEquals(projectId, result.id());
        assertEquals(projectTitle, result.title());
    }

    @Test
    void getProjectOrFailThrowsHandledFeignException() {
        FeignException feignException = new FeignException.InternalServerError(
                "Internal error",
                Request.create(Request.HttpMethod.GET, "/users/42", Map.of(), null, null, null),
                null,
                null
        );
        when(projectServiceClient.getProject(projectId)).thenThrow(feignException);
        when(feignExceptionHandler.handleFeignException(any(), any(), any())).thenThrow(new FeignClientException("Error"));

        assertThrows(FeignClientException.class, () ->  projectFeignService.getProjectOrFail(projectId));
    }



}