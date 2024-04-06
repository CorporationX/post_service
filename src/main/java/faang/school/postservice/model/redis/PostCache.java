package faang.school.postservice.model.redis;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Post", timeToLive = 86400L)
public class PostCache implements Serializable {

    @Id
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;

}