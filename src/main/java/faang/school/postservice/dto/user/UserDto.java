package faang.school.postservice.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserDto(
        @Positive
        Long id,

        @NotBlank(message = "Name can not be null or empty")
        @Max(100)
        String name,

        @NotBlank(message = "Username can not be null or empty")
        @Max(64)
        String username,

        @NotBlank(message = "E-mail can not be null or empty")
        @Email
        @Max(64)
        String email
) {
}