package faang.school.postservice.dto.post;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;
import java.util.Set;

@Data
@Builder
@RedisHash("Post")
public class PostForFeedDto {
    @Id
    private Long postId;
    private UserDto postAuthor;
    private PostDto post;
    private List<LikeDto> likesList;
    private int viewsCounter;
    private Set<CommentDto> comments;

    public void handleNewLike(LikeDto likeDto) {
        likesList.add(likeDto);
    }
}