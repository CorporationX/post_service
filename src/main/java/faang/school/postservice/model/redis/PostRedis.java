package faang.school.postservice.model.redis;

import faang.school.postservice.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class PostRedis implements Serializable {

    @Id
    private String id;
    private PostDto postDto;
    @TimeToLive
    private Long expiration;
    private Long version;

    public void incrementVersion(){
        this.version++;
    }
}
