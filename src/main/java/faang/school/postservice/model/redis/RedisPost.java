package faang.school.postservice.model.redis;

import faang.school.postservice.dto.Post.PostInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@RedisHash(value = "post")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisPost {
    @Id
    private Long id;
    private PostInfoDto postInfoDto;
    @TimeToLive (unit = TimeUnit.DAYS)
    private Long timeToLive;
    @Version
    private int version;
}
