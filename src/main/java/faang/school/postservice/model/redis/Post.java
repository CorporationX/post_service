package faang.school.postservice.model.redis;

import faang.school.postservice.dto.Post.PostInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.lang.Nullable;

@RedisHash(value = "post", timeToLive = 60)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    private Long id;
    private PostInfoDto postInfoDto;
//    private String content;
//    private Long authorId;
//    @Nullable
//    private Long projectId;
    @Version
    private int version;
}
