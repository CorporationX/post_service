package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Feed")
public class FeedForCache implements Serializable {

    private Long userId;

    private TreeSet<Long> postsIds = new TreeSet<>();


    //private TreeSet<Post> posts = new TreeSet<>(new PostComparator());

//    class PostComparator implements Comparator<Post> {
//
//        @Override
//        public int compare(Post firstPost, Post secondPost) {
//            return firstPost.getCreatedAt().compareTo(secondPost.getCreatedAt());
//        }
//    }


}
