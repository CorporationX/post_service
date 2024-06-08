package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    @Min(value = 1, message = "ID cannot be less than or equal to 0")
    private Long id;
    @NotBlank(message = "Username cannot be empty or contain whitespace characters")
    private String username;
    @Email(message = "Email should be valid")
    private String email;
}
