package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
        Long id,
        String username,
        String email
) {
}
