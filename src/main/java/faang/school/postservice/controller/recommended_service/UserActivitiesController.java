package faang.school.postservice.controller.recommended_service;

import faang.school.postservice.dto.recommended_service.UserActivitiesGetDto;
import faang.school.postservice.dto.recommended_service.UserActivitiesViewDto;
import faang.school.postservice.service.recommended_service.UserActivitiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для микросервиса {@code recommendation_service}.
 * Используется для получения активностей пользователей (по постам, лайкам, комментариям)
 *
 * @author Linempy
 * @since 23.11.2025
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/activities")
public class UserActivitiesController {

    private final UserActivitiesService service;

    @PostMapping("/batch")
    public ResponseEntity<UserActivitiesViewDto> getUserActivities(@RequestBody UserActivitiesGetDto dto) {
        UserActivitiesViewDto result = service.getUserActivities(dto);
        return ResponseEntity.ok(result);
    }

}