package faang.school.postservice.dto.user;

//public record UserDto(
//    Long id,
//    String username,
//    String email
//) {
//}

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {
        Long id;
        String username;
        String email;
}
