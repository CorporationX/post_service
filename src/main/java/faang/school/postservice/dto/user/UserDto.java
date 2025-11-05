package faang.school.postservice.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
    @NotNull(message = "id should not be null")
    Long id,
    @NotBlank(message = "username should not be blank")
    String username,
    @NotBlank(message = "email should not be blank")
    String email
) {
}
