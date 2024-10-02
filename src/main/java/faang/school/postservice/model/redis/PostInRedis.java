package faang.school.postservice.model.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.kafka.KafkaCommentEvent;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Data
@RedisHash(value = "post", timeToLive = 86400L)
public class PostInRedis implements Serializable {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private AtomicLong numberOfLikes;
    private LinkedList<KafkaCommentEvent> comments;
}
