package faang.school.postservice.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    @Min(0)
    private Long id;
    @NotBlank(message = "Это поле не должно быть пустым и не должно содержать одни пробелы")
    private String username;
    @Email(message = "Это поле должно содержать корректный адрес электронной почты")
    private String email;
    private List<Long> followers;
    private List<Long> posts;
    private List<Long> followees;
}