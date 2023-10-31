package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.Locale;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "User", timeToLive = 86400)
public class RedisUser {
    @Id
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private Integer experience;
    private List<Long> followerIds; // подписчики
    private List<Long> followeeIds; // подписки
    private Locale locale;
    @Version
    private Long version;
}
