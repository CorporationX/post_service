package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponsePostDto {
    @NotNull
    private long id;
    private String content;
    @NotNull
    private Long authorId;
    @NotNull
    private Long projectId;
    @NotEmpty
    private List<Long> likesIds;
    private List<Long> commentsIds;
    private List<Long> albumsIds;
    private Long adId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
