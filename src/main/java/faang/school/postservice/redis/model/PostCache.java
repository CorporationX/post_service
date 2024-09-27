package faang.school.postservice.redis.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.io.Serializable;

@RedisHash(value = "post", timeToLive = 86400)
@Data
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private Integer views;
    private RedisZSet<Long> comments;
}