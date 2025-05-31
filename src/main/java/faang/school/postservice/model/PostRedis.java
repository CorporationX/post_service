package faang.school.postservice.model;

import faang.school.postservice.dto.CommentRedisDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Post")
public class PostRedis implements Serializable {

    @Id
    long id;
    long authorId;
    long projectId;
    int amountLikes;

    LinkedHashSet<CommentRedisDto> latestComments = new LinkedHashSet<>();

    @TimeToLive
    private Long timeToLive;

    @Version
    private Long version = 0L;
}
