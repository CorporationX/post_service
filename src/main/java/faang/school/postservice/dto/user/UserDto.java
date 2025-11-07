package faang.school.postservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
        @NotNull(message = "User ID cannot be empty")
        Long id,
        @NotBlank(message = "Username cannot be null")
        String username,
        @NotBlank(message = "E-mail cannot be null")
        String email
) {
}
