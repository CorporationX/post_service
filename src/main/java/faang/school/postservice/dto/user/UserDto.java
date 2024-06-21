package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDto {
    @NotNull(message = "UserId can not be null")
    @Positive(message = "UserId should be positive")
    private Long id;

    @NotBlank(message = "Username can not be blank")
    private String username;

    @Email(message = "Email should be valid")
    private String email;
}
