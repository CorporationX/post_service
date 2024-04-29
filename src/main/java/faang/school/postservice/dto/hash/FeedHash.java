package faang.school.postservice.dto.hash;

import faang.school.postservice.exception.DataValidationException;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

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

    public List<Long> getNextTwentyPostsIdByLastPostId(Long lastPostId) {
        List<Long> postsId = new ArrayList<>(this.postsId);
        Collections.reverse(postsId);
        if (lastPostId == null) {
            return postsId.subList(0, 19);
        } else {
            int postIndex = postsId.indexOf(lastPostId);
            if (postIndex == -1) {
                throw new DataValidationException("Post not found : " + lastPostId);
            }
            return postsId.subList(postIndex, postIndex + 20);
        }
    }
}
