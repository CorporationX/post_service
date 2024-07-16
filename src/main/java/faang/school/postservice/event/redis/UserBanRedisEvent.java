package faang.school.postservice.event.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserBanRedisEvent implements RedisEvent {
    private List<Long> userIds;
}
