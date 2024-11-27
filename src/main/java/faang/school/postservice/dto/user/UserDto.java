package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotNull(message = "User ID cannot be null")
    private Long id;
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @Email(message = "Email must be in email format")
    private String email;
}
