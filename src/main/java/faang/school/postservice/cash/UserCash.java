package faang.school.postservice.cash;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@RedisHash("UserCash")
public class UserCash {
    @Id
    Long id;
    UserDto userDto;
}
