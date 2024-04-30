package faang.school.postservice.model.redis;

import faang.school.postservice.model.Album;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.PostView;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@RedisHash(value = "Posts")
public class RedisPost implements Serializable {
    @TimeToLive
    private long expiration;

    private long id;

    private String content;

    private Long authorId;

    private Long projectId;

    private List<Like> likes;

    private List<Comment> comments;

    private List<PostView> postViews;

    private List<Album> albums;

    private Ad ad;

    private List<Resource> resources;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
