package faang.school.postservice.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisUserDto {

    private Long userId;
    private String username;
    private String email;
    private String phone;
    private String city;
    private List<Long> followerIds;
    private List<Long> followeeIds;
}
