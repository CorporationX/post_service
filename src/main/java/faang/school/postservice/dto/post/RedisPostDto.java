package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@AllArgsConstructor
@Data
@Builder
@RedisHash(value = "Post", timeToLive = 86400)
public class RedisPostDto implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long views;
}
