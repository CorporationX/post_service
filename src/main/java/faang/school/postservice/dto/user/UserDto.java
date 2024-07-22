package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotNull
    @NotBlank
    @Size(min = 2, max = 64)
    private String username;

    @NotNull
    @NotBlank
    @Email
    @Size(max = 64)
    private String email;
}
