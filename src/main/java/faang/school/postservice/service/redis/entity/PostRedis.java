package faang.school.postservice.service.redis.entity;

import faang.school.postservice.service.redis.dto.CommentCacheDto;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.io.Serializable;
import java.util.Comparator;
import java.util.TreeSet;

@RedisHash("PostRedis")
@Data
public class PostRedis implements Serializable {

    @Id
    private Long id;

    private String content;

    private Long authorId;

    private Long likesCount;

    private TreeSet<CommentCacheDto> lastComments =
            new TreeSet<>(Comparator.comparing(CommentCacheDto::getTimestamp));

    @Version
    private Long version;
}
