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
import org.springframework.data.redis.core.RedisHash;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@RedisHash("UserDto")
public class UserDto {
        Long id;
        String username;
        String email;
}
