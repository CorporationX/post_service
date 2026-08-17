package faang.school.postservice.controller.recommended_service;

import faang.school.postservice.dto.recommended_service.UserActivitiesGetDto;
import faang.school.postservice.dto.recommended_service.UserActivitiesViewDto;
import faang.school.postservice.service.recommended_service.UserActivitiesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование REST-контроллера {@link UserActivitiesController}.
 */
@ExtendWith(MockitoExtension.class)
class UserActivitiesControllerTest {

    @Mock
    private UserActivitiesService userActivitiesService;

    @InjectMocks
    private UserActivitiesController userActivitiesController;

    @Test
    void getUserActivitiesShouldReturnActivitiesFromService() {
        UserActivitiesGetDto request = new UserActivitiesGetDto(List.of(1L), 5);
        UserActivitiesViewDto expected = new UserActivitiesViewDto(Map.of(1L, Map.of()));
        when(userActivitiesService.getUserActivities(request)).thenReturn(expected);

        ResponseEntity<UserActivitiesViewDto> response = userActivitiesController.getUserActivities(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(userActivitiesService).getUserActivities(request);
    }
}
