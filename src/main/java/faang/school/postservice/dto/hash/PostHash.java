package faang.school.postservice.dto.hash;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostHash")
public class PostHash implements Serializable {

    @Value(value = "${comments.max_count}")
    private int maxCount;

    @Id
    private Long id;

    @NotBlank(message = "Content is required")
    private String content;

    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private List<Long> followersId;
    private ConcurrentLinkedDeque<CommentDto> comments;
    private ConcurrentLinkedDeque<LikeDto> likes;
    private ConcurrentLinkedDeque<UserDto> views;

    public void addComment(CommentDto newCommentDto) {
        comments.offerLast(newCommentDto);
        if (comments.size() >= maxCount) {
            comments.pollFirst();
        }
    }
    public void addLike(LikeDto likeDto) {
        this.likes.add(likeDto);
    }

    public void addView(UserDto userDto) {
        this.views.add(userDto);
    }

    @Version
    private long version;

    @TimeToLive
    private long timeToLive;
}
