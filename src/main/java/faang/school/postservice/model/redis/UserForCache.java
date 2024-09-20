package faang.school.postservice.model.redis;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("User")
public class UserForCache implements Serializable {

    private long id;

    private String username;

    private String email;

    private String phone;

    private Long tgChatId;

    private String password;

    private boolean active;

    private String aboutMe;

    private String country;

    private String city;

    private LocalDateTime createdAt;

    private List<Long> followers;

    private List<Long> followees;
}