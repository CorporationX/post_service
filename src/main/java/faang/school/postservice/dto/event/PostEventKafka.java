package faang.school.postservice.dto.event;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEventKafka {
    private String content;
    private Long authorId;
    private Long postId;
    private Long projectId;
    private List<Long> followerIds;
    private LocalDateTime publishedAt;
    private UserDto userDto;

    public PostEventKafka(Post post, List<Long> followerIds, UserDto userDto) {
        this.content = post.getContent();
        this.authorId = post.getAuthorId();
        this.postId = post.getId();
        this.projectId = post.getProjectId();
        this.userDto = userDto;
        this.followerIds = followerIds;
    }
}
