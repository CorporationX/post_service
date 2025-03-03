package faang.school.postservice.model.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "post", timeToLive = 86400)
public class PostCache implements Serializable {

    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Integer likes;
    private Integer comments;
}
