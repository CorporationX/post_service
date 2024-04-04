package faang.school.postservice.dto.event;

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
    private LocalDateTime updatedAt;

    public PostEventKafka(Post post, List<Long> followerIds) {
        this.content = post.getContent();
        this.authorId = post.getAuthorId();
        this.postId = post.getId();
        this.projectId = post.getProjectId();
        this.updatedAt = post.getUpdatedAt();
        this.followerIds = followerIds;
    }
}
