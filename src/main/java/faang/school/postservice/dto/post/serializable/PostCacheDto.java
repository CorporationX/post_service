package faang.school.postservice.dto.post.serializable;

import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCacheDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likesIds;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Ad ad;
    private List<Long> resourceIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> hashTags;
}
