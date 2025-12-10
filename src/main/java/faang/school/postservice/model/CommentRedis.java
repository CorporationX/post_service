package faang.school.postservice.model;

import faang.school.postservice.dto.comment.CommentRedisDto;
import faang.school.postservice.dto.post.PostRedisDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.ArrayList;
import java.util.List;

@Data
@RedisHash(value = "commentRedist", timeToLive = 60000000)
public class CommentRedis {
    @Id
    private Long authorId;

    private List<CommentRedisDto> comments = new ArrayList<>();
}
