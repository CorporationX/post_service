package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
        @NotNull(message = "User ID cannot be empty")
        Long id,
        @NotBlank(message = "Username cannot be null")
        String username,
        @NotBlank(message = "E-mail cannot be null")
        String email
) {
}