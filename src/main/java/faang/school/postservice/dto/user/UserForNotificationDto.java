package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserForNotificationDto(
        @JsonProperty("id")
        long id,

        @JsonProperty("username")
        String username,

        @JsonProperty("email")
        String email,

        @JsonProperty("phone")
        String phone,

        @JsonProperty("language")
        Language language,

        @JsonProperty("preference")
        PreferredContact preference
) {
}
