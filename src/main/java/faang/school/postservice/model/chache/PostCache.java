package faang.school.postservice.model.chache;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostCache")
public class PostCache implements Serializable {
   @Id
    private Long postId;

    private String content;
    private Long authorId;
    private Long projectId;
    private LinkedHashSet<CommentCache> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private int viewCount;

    @TimeToLive
    private Long ttl;

    @Version
    private long version;
    public void incrementLike(){
     this.likeCount +=1;
     this.version++;
    }

    public void incrementView(){
     this.viewCount +=1;
     this.version++;
    }
}
