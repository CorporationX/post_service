package faang.school.postservice.model.redis;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("User")
public class UserForCache implements Serializable {

    private long id;

    private String username;

    private String email;
}