package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class UserDto {
    @NotNull(message = "User ID cannot be null")
    private Long id;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @Email(message = "Email must be in email format")
    private String email;
}
