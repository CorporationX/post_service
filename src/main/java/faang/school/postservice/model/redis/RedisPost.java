package faang.school.postservice.model.redis;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "Post", timeToLive = 86400)
public class RedisPost implements Serializable {

    @Id
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Integer likes;
    private List<RedisCommentDto> redisCommentDtos;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    @Version
    private Long version;

    public void likeIncrement(){
        likes++;
    }

    public void likeDecrement(){
        likes--;
    }
}