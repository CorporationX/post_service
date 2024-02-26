package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostEvent {
    private Long postId;
    private Long userAuthorId;
    private Long projectAuthorId;
    private String content;
    private List<Long> followerIds;
    private LocalDateTime publishedAt;

    public PostEvent(PostEvent original) {
        this.postId = original.postId;
        this.userAuthorId = original.userAuthorId;
        this.projectAuthorId = original.projectAuthorId;
        this.content = original.content;
        this.followerIds = (original.followerIds != null) ? new ArrayList<>(original.followerIds) : new ArrayList<>();
        this.publishedAt = original.publishedAt;
    }
}