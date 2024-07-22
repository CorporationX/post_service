package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    private Long id;

    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String phone;
    private boolean isActive;
}
