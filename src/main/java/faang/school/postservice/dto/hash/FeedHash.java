package faang.school.postservice.dto.hash;

import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashSet;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("FeedHash")
public class FeedHash implements Serializable {

    @Value(value = "${feed.count_post}")
    private int countPost;

    @Id
    private long userId;
    private long feedId;
    private LinkedHashSet<Long> postsId =
            new LinkedHashSet<Long>(16, .65f);

    @Version
    private long version;

    public FeedHash(long userId, long postId) {
        this.userId = userId;
        addPost(postId);
    }

    public void addPost(long postId) {
        this.postsId.add(postId);
        Iterator<Long> iterator = postsId.iterator();
        while (postsId.size() >= countPost) {
            iterator.next();
            iterator.remove();
        }
    }
}
