package faang.school.postservice.redis.model.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "author")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorCache implements Serializable {
    @Id
    private Long id;
    private String username;
    private String email;
}
