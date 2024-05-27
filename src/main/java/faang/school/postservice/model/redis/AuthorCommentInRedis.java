package faang.school.postservice.model.redis;

import faang.school.postservice.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "AuthorCommentInRedis", timeToLive = 86400L)
public class AuthorCommentInRedis implements Serializable {

        @Id
        private long id;
        private UserDto user;
}
