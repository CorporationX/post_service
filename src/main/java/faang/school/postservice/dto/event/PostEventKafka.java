package faang.school.postservice.dto.event;

import faang.school.postservice.model.Post;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEventKafka {

    @Value(value = "${comments.max_count}")
    private int maxCount;

    private Long id;

    @NotBlank(message = "Content is required")
    private String content;

    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private int likeCount;
    private List<Long> followersId;

    public PostEventKafka(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.authorId = post.getAuthorId();
        this.projectId = post.getProjectId();
        this.publishedAt = post.getPublishedAt();
        this.updatedAt = post.getUpdatedAt();
        this.likeCount = post.getLikes().size();
    }

    public void addFollowersId(List<Long> followersId) {
        this.followersId = followersId;
    }
}