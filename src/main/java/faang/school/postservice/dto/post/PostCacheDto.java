package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostCacheDto implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private long likeCount;
    private ConcurrentLinkedDeque<Long> commentIds = new ConcurrentLinkedDeque<>();
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private Boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private Boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
