package faang.school.postservice.dto.redisCache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;

@RedisHash(value = "Posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCache implements Serializable {

    @Id
    private Long postId;
    private Long authorId;
    private String content;
    private List<Long> lastCommentIds;
    private Long numberLikes;
    private Long numberViews;

    @TimeToLive
    // TODO: Проверить работает ли внедрение через @Value
//    @Value("${cache.post.ttl}")
    private Long ttl;

    public void incNumberViews(int count) {
        numberLikes += count;
    }

    public void incNumberLikes() {
        numberLikes++;
    }

    public void decNumberLikes() {
        numberLikes--;
    }

}
