package faang.school.postservice.dto.user;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@RedisHash(value = "Users")
public class UserDto {

    @Min(value = 1, message = "ID cannot be less than or equal to 0")
    @Id
    private Long id;

    @NotBlank(message = "Username cannot be empty or contain whitespace characters")
    private String username;

    @Email(message = "Email should be valid")
    private String email;
}
