package faang.school.postservice.redis.model.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.TreeSet;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PostRedisDto implements Comparable<PostRedisDto> {
    private Long id;
    private String content;
    private AuthorRedisDto author;
    private int numberOfLikes;
    private int numberOfViews;
    private TreeSet<CommentRedisDto> comments;
    private LocalDateTime publishedAt;

    @Override
    public int compareTo(PostRedisDto o) {
        return o.publishedAt.compareTo(this.publishedAt);
    }
}
