package faang.school.postservice.dto.post.serializable;

import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostJsonDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likesIds; // TODO: +
    private List<Long> commentIds; // TODO: +
    private List<Long> albumIds; // TODO: +
    private Ad ad;
    private List<Long> resourceIds; // TODO: +
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> hashTags;
}
