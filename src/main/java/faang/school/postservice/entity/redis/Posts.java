package faang.school.postservice.entity.redis;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.redis.PostDtoRedis;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("posts")
public class Posts implements Serializable {

    @Id
    private long id;
//    private PostDtoRedis postDto;
    private String content;
    private Long authorId;
    private AtomicLong likes;
    private Queue<CommentDto> comments;
    private LocalDateTime createdAt;
    @TimeToLive
    private Long ttlPosts;
}
