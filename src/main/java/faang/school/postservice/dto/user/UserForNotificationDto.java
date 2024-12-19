package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserForNotificationDto(
        long id,
        String username,
        String email,
        String phone,
        Language locale,
        PreferredContact preference
) {
}