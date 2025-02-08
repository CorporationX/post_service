package faang.school.postservice.model.event;


import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RedisHash(value = "post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostEvent {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private TreeSet<CommentEvent> comments;
    private List<Long> followersId;
    private int likesCount;
    private int viewsCount;

    @Version
    private Long version;

    @TimeToLive
    @Value("${spring.data.redis.cache.ttl}")
    private long ttl;
}
