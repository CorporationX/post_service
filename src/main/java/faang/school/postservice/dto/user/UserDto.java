package faang.school.postservice.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String username;
    private String email;
}
