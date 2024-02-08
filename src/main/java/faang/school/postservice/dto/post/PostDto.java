package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<Long> likeIds;

    private boolean published;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime publishedAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime scheduledAt;
    private boolean deleted;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime updatedAt;
}