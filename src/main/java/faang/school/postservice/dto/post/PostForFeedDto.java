package faang.school.postservice.dto.post;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentForFeedDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@RedisHash("Post")
public class PostForFeedDto implements Serializable {
    @Id
    private Long postId;

    private Long postAuthorId;

    private UserDto postAuthor;

    private String content;

    private LocalDateTime publishedAt;

    private Long viewsCounter;

    private List<LikeDto> likesList;

    private Set<CommentForFeedDto> comments;

    @Version
    private long version;

    public void handleNewLike(LikeDto likeDto) {
        if (likesList == null) {
            likesList = new ArrayList<>();
        }

        likesList.add(likeDto);

        version++;
    }

    public void handleNewComment(CommentDto commentDto, int maxCommentsAmount) {
        if (comments == null) {
            comments = new LinkedHashSet<>();
        }

        if (comments.size() >= maxCommentsAmount) {
            CommentForFeedDto firstElement = comments.iterator().next();
            comments.remove(firstElement);
        }

        comments.add(new CommentForFeedDto(commentDto, null));

        version++;
    }

    public PostForFeedDto setViewsCounter(Long viewsCounter) {
        this.viewsCounter = viewsCounter;
        version++;
        return this;
    }

    public void incrementVersion() {
        version++;
    }
}
