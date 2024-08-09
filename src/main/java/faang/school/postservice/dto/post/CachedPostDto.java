package faang.school.postservice.dto.post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash("Post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CachedPostDto implements Serializable {
    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private Long viewsQuantity;
    private int likesQuantity;
    @Version
    private Long version = 0L;

    public void setViewsQuantity(Long viewsQuantity) {
        this.viewsQuantity = viewsQuantity;
        version++;
    }

    public void incrementLikesQuantity() {
        likesQuantity += 1;
        version++;
    }
}